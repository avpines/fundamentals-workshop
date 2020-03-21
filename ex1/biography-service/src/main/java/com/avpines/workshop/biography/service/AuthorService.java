package com.avpines.workshop.biography.service;

import com.avpines.workshop.biography.model.Author;

import java.util.Collection;

public interface AuthorService {

    Collection<String> authors();

    Author biography(String author);

}
