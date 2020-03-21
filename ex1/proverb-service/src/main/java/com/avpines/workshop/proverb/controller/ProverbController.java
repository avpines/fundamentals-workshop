package com.avpines.workshop.proverb.controller;

import com.avpines.workshop.proverb.model.Proverb;
import com.avpines.workshop.proverb.service.ProverbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
public class ProverbController {

    private final ProverbService proverbService;

    public ProverbController(ProverbService proverbService) {
        this.proverbService = proverbService;
    }

    @GetMapping("/proverbs")
    Collection<Proverb> allProverbs() {
        return proverbService.allProverbs();
    }

    @GetMapping("/proverb")
    Proverb randomProverb() {
        return proverbService.randomProverb();
    }

    @GetMapping("/authors")
    Collection<String> authors() {
        return proverbService.authors();
    }

    @GetMapping("/proverb/{author}")
    Collection<Proverb> byAuthor(@PathVariable String author) {
        return proverbService.proverb(author);
    }

    @PutMapping("/proverb")
    ResponseEntity<Void> insertProverb(
            @Valid @RequestBody List<@Valid Proverb> proverbs) {
        boolean inserted = proverbService.insertProverbs(proverbs);
        return new ResponseEntity<>(inserted ?
                HttpStatus.CREATED : HttpStatus.OK);
    }

}
