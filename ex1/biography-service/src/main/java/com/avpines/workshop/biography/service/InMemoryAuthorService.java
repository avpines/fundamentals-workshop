package com.avpines.workshop.biography.service;

import com.avpines.workshop.biography.model.Author;
import com.avpines.workshop.biography.model.Biography;
import com.avpines.workshop.biography.model.Proverb;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InMemoryAuthorService implements AuthorService {

    private RestTemplate restTemplate;

    private ConcurrentMap<String, Biography> biographies;

    @Value("${biography.endpoints.proverb-service}")
    private String proverbEndpoint;

    public InMemoryAuthorService(RestTemplate restTemplate,
                                 @NotNull Collection<Biography> biographies) {
        this.restTemplate = restTemplate;
        this.biographies = biographies.stream()
                .collect(Collectors.toConcurrentMap(Biography::getName, e -> e));
    }

    @Override
    public Collection<String> authors() {
        return biographies.keySet().stream().sorted().collect(Collectors.toList());
    }

    @Override
    public Author biography(String author) {
        Biography biography = biographies.getOrDefault(author, emptyBiography(author));
        Author.AuthorBuilder b = Author.builder().biography(biography);
        Proverb[] proverbs = restTemplate.getForObject(endpoint(author), Proverb[].class);
        if (proverbs != null) {
            b.proverbs(Arrays.stream(proverbs)
                    .map(Proverb::getProverb).collect(Collectors.toList()));
        }
        return b.build();
    }

    // return the proverb-service endpoint
    // request for the specific author
    @NotNull
    private String endpoint(String author) {
        return proverbEndpoint + author;
    }

    private Biography emptyBiography(String author) {
        return Biography.builder()
                .name(author)
                .summary("No known summary")
                .build();
    }

}
