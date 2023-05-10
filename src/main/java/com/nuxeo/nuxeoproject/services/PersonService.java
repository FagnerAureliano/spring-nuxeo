package com.nuxeo.nuxeoproject.services;

import org.nuxeo.client.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    NuxeoService nuxeoService;

    public Optional<Document> findAllDocumentsByUID(String uuidNuxeo) throws IOException {
        return nuxeoService.findDocumentByUID(uuidNuxeo);
    }

    public List<Document> findAllDocumentsByTitle(String words) throws IOException {
        return nuxeoService.findDocumentByTitle(words);
    }

    public List<Document> findAllDocumentsByTags(List<String> tags) throws IOException {
        return nuxeoService.findDocumentsByTags(tags);
    }

    public void createFolder(String name) throws IOException{
        nuxeoService.createFolder(name);
    }
    public void createFile(String name) throws IOException{
        nuxeoService.createFile(name);
    }
    public String createDocumentWithFile(MultipartFile file) throws IOException{
        return nuxeoService.createDocumentWithFile(file);
    }
}
