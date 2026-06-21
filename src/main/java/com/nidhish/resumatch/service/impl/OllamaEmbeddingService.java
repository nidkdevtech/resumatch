package com.nidhish.resumatch.service.impl;

import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.nidhish.resumatch.service.EmbeddingService;

@Service
@ConditionalOnProperty(name = "ai.embedding.provider", havingValue = "ollama")
public class OllamaEmbeddingService implements EmbeddingService {

    private final OllamaEmbeddingModel embeddingModel;

    public OllamaEmbeddingService(@Value("${ai.embedding.ollama.base-url}") String baseUrl,
                                  @Value("${ai.embedding.ollama.model}") String modelName) {
        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }

    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text)
                .content()
                .vector();
    }
}
