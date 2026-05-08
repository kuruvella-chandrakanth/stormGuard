package com.example.stormGuard.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class DocumentIngestionService {

    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void ingestDocuments() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:documents/*");

        if (resources.length == 0) {
            log.warn("No documents found in classpath:documents/ folder. Skipping ingestion.");
            return;
        }

        TokenTextSplitter splitter = new TokenTextSplitter(false);

        for (Resource resource : resources) {
            log.info("Ingesting document: {}", resource.getFilename());
            try {
                TikaDocumentReader reader = new TikaDocumentReader(resource);
                List<Document> documents = reader.get();
                List<Document> chunks = splitter.apply(documents);
                vectorStore.add(chunks);
                log.info("Ingested {} chunks from: {}", chunks.size(), resource.getFilename());
            } catch (Exception e) {
                log.error("Failed to ingest document: {}", resource.getFilename(), e);
            }
        }

        log.info("Document ingestion complete.");
    }
}
