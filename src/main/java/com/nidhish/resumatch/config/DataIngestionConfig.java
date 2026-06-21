package com.nidhish.resumatch.config;

import com.nidhish.resumatch.service.JobIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataIngestionConfig {

    @Bean
    CommandLineRunner runIngestion(JobIngestionService jobIngestionService) {
        return args -> {
            String filePath = "src/main/resources/data/job_postings.csv";
            int limit = 100; // start small for testing
            System.out.println("Starting job ingestion...");
            jobIngestionService.ingestJobs(filePath, limit);
        };
    }
}