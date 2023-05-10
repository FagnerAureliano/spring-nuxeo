package com.nuxeo.nuxeoproject.controllers;

import com.nuxeo.nuxeoproject.services.PersonService;
import org.nuxeo.client.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

    @GetMapping("/documents-uid/{uid}")
    public Optional<Document> findByUID(@PathVariable(name = "uid") String uid) throws IOException {
        return personService.findAllDocumentsByUID(uid);
    }

    @GetMapping("/documents-title/{name}")
    public List<Document> findAllByTitle(@PathVariable(name = "name") String name) throws IOException {
        return personService.findAllDocumentsByTitle(name);
    }

    @GetMapping("/documents-tags/{tags}")
    public List<Document> findAllByTags(@PathVariable(name = "tags") List<String> tags) throws IOException {
        return personService.findAllDocumentsByTags(tags);
    }

    @PostMapping("/documents-folder/{name}")
    public void createFolder(@PathVariable(name = "name") String name) throws IOException {
        personService.createFolder(name);
    }

    @PostMapping("/documents-file/{name}")
    public void createFile(@PathVariable(name = "name") String name) throws IOException {
        personService.createFile(name);
    }
    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createDocumentWithFile( @RequestPart(name = "arquivo") MultipartFile multipartFile) throws IOException {
        // create a File object from the uploaded MultipartFile
//        File uploadedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
//        FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
//        fileOutputStream.write(file.getBytes());
//        fileOutputStream.close();

        // call the service method to create the document
        String document = personService.createDocumentWithFile(multipartFile);

        // return a response
        String response = "Document created with ID: " + document;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
