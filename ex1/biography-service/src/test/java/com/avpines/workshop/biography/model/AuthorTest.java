package com.avpines.workshop.biography.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class AuthorTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

    // check that the class can be serialized and deserialized back
    @Test
    void serializeDeserialize() throws Exception {
        Author author = Author.builder()
                .biography(Biography.builder().name("Hello").yearOfBirth(1983)
                        .summary("World!").build())
                .proverbs(Arrays.asList("what", "the", "proverb"))
                .build();
        String s = MAPPER.writeValueAsString(author);
        LOG.info("s is '{}'", s);
        Author actual = MAPPER.readValue(s, Author.class);
        assertThat(actual).isEqualTo(author);
    }

    @Test
    void deserializationFromWantedForm() throws Exception {
        String s = "{'biography':{'name':'Hello','summary':'World!','year_of_birth':1983}," +
                "'proverbs':['what','the','proverb']}";
        Author actual = MAPPER.readValue(s, Author.class);
        assertThat(actual.getBiography().getName()).isEqualTo("Hello");
        assertThat(actual.getBiography().getYearOfBirth()).isEqualTo(1983);
        assertThat(actual.getBiography().getSummary()).isEqualTo("World!");
        assertThat(actual.getProverbs()).containsExactlyInAnyOrder("what", "the", "proverb");
    }
}