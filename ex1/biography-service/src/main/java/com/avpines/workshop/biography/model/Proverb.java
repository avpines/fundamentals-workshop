package com.avpines.workshop.biography.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Proverb {

    String author;

    String proverb;

    @JsonCreator
    public Proverb(@JsonProperty("author") String author,
                   @JsonProperty("proverb") String proverb) {
        this.author = author;
        this.proverb = proverb;
    }

}
