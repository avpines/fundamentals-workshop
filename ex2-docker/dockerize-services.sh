#!/bin/bash

# This variable will point to the absolute path the this script resides in.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

#
# Builds the artifact and Docker image of given service
#  $1 - The root dir of the service to build
#  $2 - The tag to use for the service's Docker image.
#
function prepare_service() {
	local PROJECT_DIR="$1"
	local PROJECT_TAG="$2"
	echo "---------------------------------------------------------------"
	echo "PREPARING SERVICE $PROJECT_TAG"
	echo "---------------------------------------------------------------"
	mvn clean install -f "$PROJECT_DIR"
	# copy the Dockerfile to the root of the project
	cp Dockerfile "$PROJECT_DIR"
	docker build -t "$PROJECT_TAG" "$PROJECT_DIR"
	# cleanup - remove the copied Dockerfile
	rm "${PROJECT_DIR}/Dockerfile"
}

################################################################################
# Main
################################################################################

NETWORK_NAME="workshop-ex-network"
PROVERB_SERVICE_TAG="proverb-service:0.0.1-SNAPSHOT"
PROVERB_SERVICE_LOC="${DIR}/../ex1/proverb-service"
PROVERB_SERVICE_NAME="proverb-service"
PROVERB_SERVICE_EXPOSE_PORT=8080
BIOGRAPHY_SERVICE_TAG="biography-service:0.0.1-SNAPSHOT"
BIOGRAPHY_SERVICE_LOC="${DIR}/../ex1/biography-service"
BIOGRAPHY_SERVICE_NAME="biography-service"
BIOGRAPHY_SERVICE_EXPOSE_PORT=8081

# Build artifacts and Docker images
prepare_service "$PROVERB_SERVICE_LOC" "$PROVERB_SERVICE_TAG"
prepare_service "$BIOGRAPHY_SERVICE_LOC" "$BIOGRAPHY_SERVICE_TAG"

echo "---------------------------------------------------------------"
echo "CREATING NETWORK $NETWORK_NAME"
echo "---------------------------------------------------------------"

# Deleted networks and containers from previous runs
docker rm -f "$PROVERB_SERVICE_NAME"
docker rm -f "$BIOGRAPHY_SERVICE_NAME"
docker network rm "$NETWORK_NAME"

# Create a new network
docker network create "$NETWORK_NAME"

# Run the proverb service on the network we've created
docker run \
	--name "$PROVERB_SERVICE_NAME" -d -P \
	--expose "$PROVERB_SERVICE_EXPOSE_PORT" \
	--network "$NETWORK_NAME" \
	"$PROVERB_SERVICE_TAG"

# Run the biography service, on the network that we have created
# and point it to the proverb service that we've just ran by
# setting the environment variable $PROVERB_SERVICE_URL.
docker run -d -P \
	--name "$BIOGRAPHY_SERVICE_NAME" \
	--expose "$BIOGRAPHY_SERVICE_EXPOSE_PORT" \
	--network "$NETWORK_NAME" \
	--env PROVERB_SERVICE_URL="http://${PROVERB_SERVICE_NAME}:${PROVERB_SERVICE_EXPOSE_PORT}" \
	"$BIOGRAPHY_SERVICE_TAG"