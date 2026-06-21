package com.nidhish.resumatch.service;

import com.nidhish.resumatch.dto.JobMatchResult;

import java.util.List;

public interface JobMatchingService {
    List<JobMatchResult> findMatches(byte[] resumePdfBytes, int topK);
}
