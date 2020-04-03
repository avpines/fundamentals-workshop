package com.avpines.workshop.proverb.controller;

import com.avpines.workshop.proverb.model.Proverb;
import com.avpines.workshop.proverb.service.ProverbService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
public class ProverbController {

    private final ProverbService proverbService;

    @Autowired
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

    // if a validation error occurred, ConstraintViolationException will be
    // thrown and this method will catch it and handle it, returning a bad
    // request (400) back to the client.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> handle(@NotNull ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errors = violations.stream()
                .map(
                        v -> {
                            String path = v.getPropertyPath().toString();
                            // remove the name of the method itself, leave only
                            // the actual path, e.g. 'proverbs[0].author`
                            return path.substring(path.indexOf('.') + 1) + ": " + v.getMessage();
                        }
                ).collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
