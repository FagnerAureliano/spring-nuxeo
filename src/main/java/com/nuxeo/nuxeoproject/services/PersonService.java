package com.nuxeo.nuxeoproject.services;

import org.nuxeo.client.objects.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    NuxeoService nuxeoService;

    public Optional<Document> findAllDocumentoByUID(String uuidNuxeo) throws IOException {
        return nuxeoService.findDocumentByUID(uuidNuxeo);
    }
    public List<Document> findAllDocumentoByWords(String words) throws IOException {
        return nuxeoService.findDocumentByWord(words);
    }
    public List<Document> findAllDocumentoByTags(List<String> tags) throws IOException {
        return nuxeoService.findDocumentsByTags(tags);
    }

}
