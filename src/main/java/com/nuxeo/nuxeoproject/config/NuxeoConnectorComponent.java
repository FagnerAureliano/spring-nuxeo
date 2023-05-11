package com.nuxeo.nuxeoproject.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.objects.upload.BatchUploadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NuxeoConnectorComponent {
    private static final Logger logger = LoggerFactory.getLogger(NuxeoConnectorComponent.class);

    private final String server;
    private final String username;
    private final String password;
    private final String path;
    private BatchUploadManager batchUploadManager;
    private NuxeoClient nuxeoClient = null;

    public NuxeoConnectorComponent(
            @Value("${nuxeo.server}") String server,
            @Value("${nuxeo.username}") String username,
            @Value("${nuxeo.password}") String password,
            @Value("${nuxeo.path}") String path
    ) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.path = path;
        connect();
    }

    private void connect() {
        if (null == nuxeoClient) {
            nuxeoClient = new NuxeoClient.Builder().url(server).authentication(username, password).connect();
            nuxeoClient.schemas("*");
            nuxeoClient.timeout(1000);
        }
        logger.info("Nuxeo Conectado");
    }
    @PostConstruct
    private void postConstruct() {
        batchUploadManager = nuxeoClient.batchUploadManager();
    }

    @PreDestroy
    public void disconnect() {
        if (null != nuxeoClient) {
            nuxeoClient.disconnect();
        }
        logger.info("Nuxeo Desconectado");
    }

    public BatchUpload getBatchUpload() {
        return batchUploadManager.createBatch();
    }

    public NuxeoClient getNuxeoClient() {
        return nuxeoClient;
    }

    public String getDefaultPath() {
        return this.path;
    }

    public NuxeoClient nuxeoClient() {
        return getNuxeoClient();
    }
}
