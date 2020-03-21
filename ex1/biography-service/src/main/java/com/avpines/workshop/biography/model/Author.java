package com.avpines.workshop.biography.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Value
public class Author {

    Biography biography;

    List<String> proverbs;

    @Builder
    private Author(@JsonProperty("biography") Biography biography,
                   @JsonProperty("proverbs") List<String> proverbs) {
        this.biography = biography;
        this.proverbs = proverbs;
    }

}
