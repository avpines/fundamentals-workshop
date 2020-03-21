package com.avpines.workshop.biography.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
public class Biography {

    String name;

    @JsonProperty("year_of_birth")
    Integer yearOfBirth;

    String summary;

    @Builder
    private Biography(@JsonProperty("name") String name,
                      @JsonProperty("year_of_birth") Integer yearOfBirth,
                      @JsonProperty("summary") String summary) {
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.summary = summary;
    }

}
