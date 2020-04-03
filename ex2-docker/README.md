# Docker Fundamentals -  Exercise Solution

A shell script that is doing all that is detailed here can be found under this
directory named `dockerize-services.sh`.

Throughout this solution we assume that we reside in the same directory as
this file, i.e. `ex2-docker`.

## Additions to our Biography Service

Our original Biography-Service received the location of the Proverb service as a
configuration parameters in its `application.yaml`:

```yaml
biography:
  endpoints:
    proverb-service: http://localhost:8080/proverb/
```

We have tweaked it a little bit to accept an environment variable 
`$PROVERB_SERVICE_URL` as the value for that param, and if that value does not 
exist, keep the old value as a default:

```yaml
biography:
  endpoints:
    proverb-service: ${PROVERB_SERVICE_URL:http://localhost:8080}/proverb/
```

## Dockerfile

We create a basic `Dockerfile` that will serve both services:

```dockerfile
FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

As we can see it doesn't expose any port, we will do it later when we spin up
a container.

## Prepare the Docker Images

### Proverb Service

#### Create the service artifact

First we need to create a Jar that will be used when building the image. From
the root of the proverb-service project run:

```shell script
$ mvn clean install -f "../ex1/proverb-service"
```

#### Create the Docker Image

The Dockerfile assumes that it is placed in the root directory as well, so we
will copy it there first:

```shell script
$ cp Dockerfile ../ex1/proverb-service
```

and then we can build it

```shell script
$ docker build -t proverb-service:0.0.1-SNAPSHOT ../ex1/proverb-service
```

verify that the image was created

```shell script
$ docker image ls
REPOSITORY       TAG             IMAGE ID      CREATED         SIZE
proverb-service  0.0.1-SNAPSHOT  956f29c77993  11 seconds ago  421MB
```

### Biography Service

We will do the same steps that we have done for the Proverb Service:

```shell script
$ mvn clean install -f "../ex1/biography-service"
$ cp Dockerfile ../ex1/biography-service
$ docker build -t biography-service:0.0.1-SNAPSHOT ../ex1/biography-service
```

verify that the image was created

```shell script
$ docker image ls
REPOSITORY        TAG             IMAGE ID      CREATED         SIZE
biogrphy-service  0.0.1-SNAPSHOT  ed49077d2f60  42 seconds ago  421MB
```

## Network

Next we will create a network for both our container to communicate with each
other on:

```shell script
$ docker network create workshop
```

verify

```shell script
$ docker network ls
NETWORK ID    NAME      DRIVER  SCOPE
df83701bedfd  workshop  bridge  local
```

## Spin Up the Containers

### Proverb Service

We need to run the service on the network that we've just created, and make sure
that we expose port 8080 which the service runs on:

```shell script
$ docker run \
  --name "proverb-service" -d -P \
  --expose 8080 \
  --network "workshop" \
  "proverb-service:0.0.1-SNAPSHOT"
```

Verify that the container is up

```plain
$ docker container ls
CONTAINER ID  IMAGE                             COMMAND                CREATED             STATUS         PORTS                     NAMES
0f69e7f21a82  proverb-service:0.0.1-SNAPSHOT    "java -jar /app.jar"   18 seconds ago      Up 16 seconds  0.0.0.0:32775->8080/tcp   proverb-service
```

### Biography Service

Biography service also needs to run on the same network, and expose port 8081.
In addition, since it needs to communicate with the Proverb service, we will set
the environment variable `$PROVERB_SERVICE_URL` for the location of the Proverb
service.

```shell script
$ docker run -d -P \
  --name "biography-service" \
  --expose 8081 \
  --network "workshop" \
  --env PROVERB_SERVICE_URL="http://proverb-service:8080" \
  "biography-service:0.0.1-SNAPSHOT"
```

Verify that the container is up

```plain
$ docker container ls
CONTAINER ID  IMAGE                             COMMAND                CREATED             STATUS         PORTS                     NAMES
f6e26fa9a6ef  biography-service:0.0.1-SNAPSHOT  "java -jar /app.jar"   7 seconds ago       Up 5 seconds   0.0.0.0:32776->8081/tcp   biography-service
```

## Let's Check that Everything Works

1. Connect to the Proverb service:

    ```shell script
    $ curl http://localhost:32775/proverb | jq .
    ```
    ```json
    {
      "author": "Ralph Johnson",
      "proverb": "Before software can be reusable it first has to be usable"
    }
    ```

2. Connect to Biography's `/biography/{author}` endpoint, to see if it can connect
    to the Proverb service:
    
    ```shell script
    $ curl http://localhost:32776/biography/Ralph%20Johnson | jq .
    ```
    ```json
    {
      "biography": {
        "name": "Ralph Johnson",
        "year_of_birth": 1923,
        "summary": "Medical student who enjoys spreading fake news on Facebook, horse riding and playing card games."
      },
      "proverbs": [
        "Before software can be reusable it first has to be usable"
      ]
    }
    ```

