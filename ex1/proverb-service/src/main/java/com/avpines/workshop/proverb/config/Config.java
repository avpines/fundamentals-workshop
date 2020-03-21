package com.avpines.workshop.proverb.config;

import com.avpines.workshop.proverb.model.Proverb;
import com.avpines.workshop.proverb.service.InMemoryProverbService;
import com.avpines.workshop.proverb.service.ProverbService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Configuration
public class Config {

    @Bean
    Random random() {
        return new Random();
    }

    @Bean
    ProverbService proverbService(Random random) {
        return new InMemoryProverbService(random, proverbs());
    }

    @NotNull
    private Collection<Proverb> proverbs() {
        return Arrays.asList(
                        Proverb.builder()
                                .proverb("Before software can be reusable it first has to be usable")
                                .author("Ralph Johnson")
                                .build(),
                        Proverb.builder()
                                .proverb("One man’s crappy software is another man’s full-time job")
                                .author("Jessica Gaston")
                                .build(),
                        Proverb.builder()
                                .proverb("Deleted code is debugged code")
                                .author("Jeff Sickel")
                                .build(),
                        Proverb.builder()
                                .proverb("If at first you don’t succeed; call it version 1.0")
                                .author("Anonymous")
                                .build(),
                        Proverb.builder()
                                .proverb("It’s not a bug – it’s an undocumented feature")
                                .author("Anonymous")
                                .build());
    }

}
