package com.avpines.workshop.proverb.service;

import com.avpines.workshop.proverb.model.Proverb;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class InMemoryProverbServiceTest {

    InMemoryProverbService proverbService;

    Random rnd = mock(Random.class);

    @BeforeEach
    void setup() {
        reset(rnd);
        proverbService = new InMemoryProverbService(rnd, mockProverbs());
    }

    @Test
    void randomProverb() {
        when(rnd.nextInt(anyInt())).thenReturn(1);
        assertThat(proverbService.randomProverb())
                .isEqualTo(mockProverbs().get(1));
    }

    @Test
    void allProverbs() {
        assertThat(proverbService.allProverbs())
                .containsExactlyInAnyOrder(mockProverbs().toArray(new Proverb[0]));
    }

    @Test
    void authors() {
        assertThat(proverbService.authors()).containsExactlyInAnyOrder(
                mockProverbs().stream().map(Proverb::getAuthor)
                        .collect(Collectors.toSet()).toArray(String[]::new));
    }

    @Test
    void insertProverbs() {
        Proverb firstProverb = Proverb.builder().author("hello").proverb("world").build();
        Proverb secondProverb = Proverb.builder().author("hello2").proverb("world2").build();
        List<Proverb> proverbs = Arrays.asList(firstProverb, secondProverb);
        proverbService.insertProverbs(proverbs);
        assertThat(proverbService.allProverbs()).containsExactlyInAnyOrder(
                Stream.concat(mockProverbs().stream(), proverbs.stream()).toArray(Proverb[]::new));
    }

    // attempting to insert the same proverb will result in only
    // one being inserted. no duplicate proverbs allowed
    @Test
    void insertProverbsNoDuplicates() {
        Proverb firstProverb = Proverb.builder().author("hello").proverb("world").build();
        Proverb secondProverb = Proverb.builder().author("hello").proverb("world").build();
        List<Proverb> proverbs = Arrays.asList(firstProverb, secondProverb);
        proverbService.insertProverbs(proverbs);
        List<Proverb> expected = new ArrayList<>(mockProverbs());
        // add only one of them to the expected.
        expected.add(firstProverb);
        assertThat(proverbService.allProverbs())
                .containsExactlyInAnyOrder(expected.toArray(Proverb[]::new));
    }

    @NotNull
    private List<Proverb> mockProverbs() {
        return Arrays.asList(
                Proverb.builder()
                        .proverb("Once upon a midnight dreary")
                        .author("Edgar Allan")
                        .build(),
                Proverb.builder()
                        .proverb("While I pondered weak and weary")
                        .author("Edgar Allan")
                        .build(),
                Proverb.builder()
                        .proverb("Over many a quaint and curious volume")
                        .author("Poe")
                        .build(),
                Proverb.builder()
                        .proverb("of forgotten Spring lore")
                        .author("Poe")
                        .build(),
                Proverb.builder()
                        .proverb("While I nodded, nearly napping")
                        .author("E.A.P")
                        .build()
        );
    }
}