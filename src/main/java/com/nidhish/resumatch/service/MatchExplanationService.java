package com.nidhish.resumatch.service;

import com.nidhish.resumatch.dto.JobMatchResult;

import java.util.List;

public interface MatchExplanationService {
    List<JobMatchResult> enrichWithExplanation(String resumeText, List<JobMatchResult> jobMatchResults);
}
