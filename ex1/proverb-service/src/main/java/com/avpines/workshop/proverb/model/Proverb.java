package com.avpines.workshop.proverb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Comparator;

@Value
public class Proverb implements Comparable<Proverb> {

    @NotEmpty
    @Size(min = 1)
    String author;

    @NotEmpty
    @Size(min = 5)
    String proverb;

    @Builder
    private Proverb(@JsonProperty("author") String author,
                    @JsonProperty("proverb") String proverb) {
        this.author = author;
        this.proverb = proverb;
    }

    @Override
    public int compareTo(@org.jetbrains.annotations.NotNull Proverb o) {
        return Comparator.comparing(Proverb::getAuthor)
                .thenComparing(Proverb::getProverb)
                .compare(this, o);
    }

}
