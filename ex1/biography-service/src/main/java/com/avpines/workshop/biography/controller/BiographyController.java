package com.avpines.workshop.biography.controller;

import com.avpines.workshop.biography.model.Author;
import com.avpines.workshop.biography.service.AuthorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class BiographyController {

    private final AuthorService authorService;

    public BiographyController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/authors")
    Collection<String> authors() {
        return authorService.authors();
    }

    @GetMapping("/biography/{author}")
    Author byAuthor(@PathVariable String author) {
        return authorService.biography(author);
    }

}
