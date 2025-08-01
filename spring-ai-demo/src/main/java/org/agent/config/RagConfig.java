package org.agent.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@Configuration
public class RagConfig {
    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) throws IOException {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        Resource[] resources = new PathMatchingResourcePatternResolver()
                .getResources("classpath:doc/*");
        List<Document> documents = new ArrayList<>();
        for (Resource resource : resources) {
            TextReader reader = new TextReader(resource);
            documents.addAll(reader.get());
        }
        vectorStore.add(documents);
        return vectorStore;
    }
}
