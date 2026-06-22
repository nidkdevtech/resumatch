package com.nidhish.resumatch.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${ai.llm.provider}")
    private String provider;

    @Value("${ai.llm.groq.api-key}")
    private String groqApiKey;

    @Value("${ai.llm.groq.model:llama-3.3-70b-versatile}")
    private String groqModel;

    @Value("${ai.llm.gemini.api-key}")
    private String geminiApiKey;

    @Value("${ai.llm.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if ("gemini".equals(provider)) {
            return OpenAiChatModel.builder()
                    .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/")
                    .apiKey(geminiApiKey)
                    .modelName(geminiModel)
                    .temperature(0.3)
                    .build();
        }
        return OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(groqApiKey)
                .modelName(groqModel)
                .temperature(0.3)
                .build();
    }
}

