package com.avpines.workshop.proverb.service;

import com.avpines.workshop.proverb.model.Proverb;

import java.util.Collection;

public interface ProverbService {

    Collection<Proverb> allProverbs();

    Proverb randomProverb();

    Collection<String> authors();

    Collection<Proverb> proverb(String author);

    boolean insertProverbs(Collection<Proverb> proverb);

}
