package com.avpines.workshop.biography.config;

import com.avpines.workshop.biography.model.Biography;
import com.avpines.workshop.biography.service.AuthorService;
import com.avpines.workshop.biography.service.InMemoryAuthorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;

@Configuration
public class BiographyConfig {

    @Bean
    AuthorService authorService(RestTemplate restTemplate) {
        return new InMemoryAuthorService(restTemplate, biographies());
    }

    @NotNull
    private Collection<Biography> biographies() {
        return Arrays.asList(
                Biography.builder()
                        .name("Ralph Johnson")
                        .yearOfBirth(1923)
                        .summary("Medical student who enjoys spreading fake news " +
                                "on Facebook, horse riding and playing card games.")
                        .build(),
                Biography.builder()
                        .name("Jessica Gaston")
                        .yearOfBirth(1985)
                        .summary("A former health center receptionist who\n" +
                                "enjoys vandalising bus stops, photography and " +
                                "colouring books.")
                        .build(),
                Biography.builder()
                        .name("Jeff Sickel")
                        .yearOfBirth(1998)
                        .summary(("A 22-year-old business studies student who " +
                                "enjoys horse riding, duck herding and badminton"))
                        .build()
        );
    }

}
