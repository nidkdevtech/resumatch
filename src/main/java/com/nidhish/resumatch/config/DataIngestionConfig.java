package com.nidhish.resumatch.config;

import com.nidhish.resumatch.service.JobIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataIngestionConfig {

    @Bean
    @ConditionalOnProperty(name = "app.ingestion.enabled", havingValue = "true")
    CommandLineRunner runIngestion(JobIngestionService jobIngestionService) {
        return args -> {
            String filePath = "src/main/resources/data/job_postings.csv";
            int limit = 1000; // start small for testing
            System.out.println("Starting job ingestion...");
            jobIngestionService.ingestJobs(filePath, limit);
        };
    }
}