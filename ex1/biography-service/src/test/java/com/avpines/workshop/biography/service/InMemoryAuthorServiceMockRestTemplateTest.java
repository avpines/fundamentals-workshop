package com.avpines.workshop.biography.service;

import com.avpines.workshop.biography.config.BiographyConfig;
import com.avpines.workshop.biography.model.Author;
import com.avpines.workshop.biography.model.Proverb;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static com.avpines.workshop.biography.service.InMemoryAuthorServiceMockRestTemplateTest.RestTemplateTestConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes =
        {
                BiographyConfig.class,
                RestTemplateTestConfig.class
        }
)
class InMemoryAuthorServiceMockRestTemplateTest {

    @Configuration
    static class RestTemplateTestConfig {

        @Bean
        RestTemplate restTemplate() {
            return mock(RestTemplate.class);
        }

    }

    @Value("${biography.endpoints.proverb-service}")
    private String proverbEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthorService authorService;

    @PostConstruct
    private void printEndpoint() {
        LOG.info("Proverb endpoint is '{}'", proverbEndpoint);
    }

    @Test
    void authorBiography() {
        String name = "Ralph Johnson";
        when(restTemplate.getForObject(proverbEndpoint(name), Proverb[].class))
                .thenReturn(new Proverb[] {new Proverb(name, "hello, world")});
        Author actual = authorService.biography(name);
        // assert against the biographies that we have in the config
        assertThat(actual.getBiography().getName()).isEqualTo(name);
        assertThat(actual.getBiography().getYearOfBirth()).isEqualTo(1923);
        assertThat(actual.getBiography().getSummary())
                .isEqualTo("Medical student who enjoys spreading fake news " +
                        "on Facebook, horse riding and playing card games.");
        assertThat(actual.getProverbs()).containsExactlyInAnyOrder("hello, world");
    }

    String proverbEndpoint(String author) {
        return proverbEndpoint + author;
    }

}