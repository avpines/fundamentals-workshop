package com.avpines.workshop.proverb.service;

import com.avpines.workshop.proverb.model.Proverb;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InMemoryProverbService implements ProverbService {

    private Random rnd;

    private Set<Proverb> proverbSet;

    private ConcurrentMap<String, List<Proverb>> proverbMap;

    public InMemoryProverbService(Random rnd,
                                  Collection<Proverb> proverbs) {
        this.proverbSet = new HashSet<>(proverbs);
        this.proverbMap = mappify(proverbSet);
        this.rnd = rnd;
    }

    @Override
    public Collection<Proverb> allProverbs() {
        return proverbSet;
    }

    @Override
    public Proverb randomProverb() {
        return new ArrayList<>(proverbSet).get(rnd.nextInt(proverbMap.size()));
    }

    @Override
    public Collection<String> authors() {
        return proverbMap.keySet().stream().sorted().collect(Collectors.toList());
    }

    @Override
    public Collection<Proverb> proverb(String author) {
        return proverbMap.getOrDefault(author, new ArrayList<>())
                .stream().sorted().collect(Collectors.toList());
    }

    @Override
    public boolean insertProverbs(@NotNull Collection<Proverb> proverbs) {
        // not a very rigorous check i admit.
        boolean inserted = proverbs.stream()
                .map(p -> proverbSet.add(p))
                .reduce(false, (a, b) -> a || b);
        proverbMap = mappify(proverbSet);
        return inserted;
    }

    private ConcurrentMap<String, List<Proverb>> mappify(
            @NotNull Collection<Proverb> proverbs) {
        return proverbs.stream()
                .collect(Collectors.groupingByConcurrent(Proverb::getAuthor));
    }

}
