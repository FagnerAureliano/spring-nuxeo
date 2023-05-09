package com.nuxeo.nuxeoproject.controllers;

import com.nuxeo.nuxeoproject.services.PersonService;
import org.nuxeo.client.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1")
public class PersonController {

    @Autowired
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/documents/{uid}")
    public Optional<Document> findByUID(@PathVariable(name = "uid") String uid) throws IOException {
        return personService.findAllDocumentoByUID(uid);
    }

    @GetMapping("/documents-name/{name}")
    public List<Document> findAllByName(@PathVariable(name = "name") String name) throws IOException {
        return personService.findAllDocumentoByWords(name);
    }

    @GetMapping("/documents-tags/{tags}")
    public List<Document> findAllByTags(@PathVariable(name = "tags") List<String> tags) throws IOException {
        List<String> tagsTest = Arrays.asList("alguma", "testando");
        return personService.findAllDocumentoByTags(tagsTest);
    }
}
