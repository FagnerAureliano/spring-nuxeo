package com.nuxeo.nuxeoproject.services;

import com.nuxeo.nuxeoproject.config.NuxeoConnectorComponent;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Documents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NuxeoService {

    @Autowired
    private NuxeoConnectorComponent connector;


    public Optional<Document> findDocumentByUID(String uuIdNuxeo) throws IOException {
        return Optional.ofNullable(connector.getNuxeoClient().repository().fetchDocumentById(uuIdNuxeo));
    }

    public List<Document> findDocumentByWord(String searchWord) throws IOException {
        // Constrói a consulta
        String query = "SELECT * FROM Document WHERE dc:title LIKE '%" + searchWord + "%'";
        // Executa a consulta
        Documents docs = connector.getNuxeoClient().repository().query(query);
        // Retorna o primeiro documento da lista de resultados, se houver algum
        return docs.streamEntries().collect(Collectors.toList());
    }

    public List<Document> findDocumentsByTags(List<String> tags) throws IOException {

        List<Document> results = new ArrayList<>();
        System.out.println(tags.toArray().length);
        for (int i = 0; i < 10; i++) {
            String query = "Select * From Document WHERE ecm:tag  = '" + tags.toArray() + "'";
            Documents documents = connector.getNuxeoClient().repository().query(query);
            results.addAll(documents.getDocuments());
        }
        return results;
    }


}
