package com.nidhish.resumatch.service;

public interface JobIngestionService {
    void ingestJobs(String filePath, int limit);
}
