package com.nuxeo.nuxeoproject.services;

import com.nuxeo.nuxeoproject.config.NuxeoConnectorComponent;
import jakarta.el.PropertyNotFoundException;
import org.nuxeo.client.Operations;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Documents;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.objects.upload.BatchUploadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NuxeoService {

    @Autowired
    private NuxeoConnectorComponent connector;


    public Optional<Document> findDocumentByUID(String uuIdNuxeo) throws IOException {
        return Optional.ofNullable(connector.getNuxeoClient().repository().fetchDocumentById(uuIdNuxeo));
    }

    public List<Document> findDocumentByTitle(String searchWord) throws IOException {
        // Build the query
        String query = String.format(
                "SELECT * FROM Document WHERE ecm:fulltext LIKE '%s' AND ecm:mixinType != 'HiddenInNavigation'",
                searchWord
        );

        // Execute the query
        Documents docs = connector.getNuxeoClient().repository().query(query);

        // Convert the results to a list of documents
        return docs.getDocuments();
    }

    public List<Document> findDocumentsByTags(List<String> tags) throws IOException {
        Set<Document> resultSet = new HashSet<>();
        for (String tag : tags) {
            String query = String.format("SELECT * FROM Document WHERE ecm:tag = '%s'", tag);
            Documents documents = connector.getNuxeoClient().repository().query(query);
            resultSet.addAll(documents.getDocuments());
        }
        return new ArrayList<>(resultSet);
    }

    public void createFolder(String folderName) throws IOException {
        Document folder = Document.createWithName(folderName, "Folder");
        folder = connector.nuxeoClient().repository().createDocumentByPath(connector.getDefaultPath(), folder);
    }

    public void createFile(String nameFile) throws IOException {
        Document file = Document.createWithName(nameFile, "File");
//        file.setPropertyValue("name",);
        file = connector.nuxeoClient().repository().createDocumentByPath(connector.getDefaultPath() + "teste", file);
    }

    public String createDocumentWithFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Document document = Document.createWithName(fileName, "File");
        String contentType = file.getContentType();
        document.setPropertyValue("fi:titulo", fileName);

        Document savedDocument = connector.getNuxeoClient().repository().createDocumentByPath(connector.getDefaultPath(), document);

        connector
                .getNuxeoClient()
                .operation(Operations.BLOB_ATTACH_ON_DOCUMENT)
                .voidOperation(true) // allows to not download blob in response
                .param("document", savedDocument.getPath()) // document to attach
                .param("xpath", "file:content") // xpath to store blobs
                .input(new StreamBlob(file.getInputStream(), fileName, contentType))
                .execute();

        return savedDocument.getId();
    }


    public Blob getBlobForDocument(String documentId) throws IOException {
        Document doc = connector.nuxeoClient().repository().fetchDocumentById(documentId);
        Blob blob = (Blob) doc.getProperties().get("file:content");
        return blob;
    }

    public List<Document> findDocumentByContent(String searchWord) throws IOException {
        // Build the query
        String query = String.format(
                "SELECT * FROM Document WHERE ecm:fulltext ILIKE '%s' AND ecm:mixinType != 'HiddenInNavigation' AND ecm:primaryType = 'File' AND file:content/height IS NULL",
                searchWord
        );

        // Execute the query
        Documents docs = connector.getNuxeoClient().repository().query(query);
        List<Document> documents = docs.getDocuments();

        List<Document> result = new ArrayList<>();

        for (Document doc : documents) {
            // Get the file content as a blob
            Blob fileContent = getBlobForDocument(doc.getId());

            // Check if the file content is a PDF
            if (fileContent.getMimeType().equals("application/pdf")) {
                // Get the text content of the PDF
                String textContent = null;
                try {
                    textContent = (String) doc.getPropertyValue("file:content/OCR/text");
                } catch (PropertyNotFoundException e) {
                    // OCR not available, skip this document
                    continue;
                }

                // Check if the search word appears in the text content
                if (textContent != null && textContent.toLowerCase().contains(searchWord.toLowerCase())) {
                    result.add(doc);
                }
            }
        }

        return result;
    }


}
