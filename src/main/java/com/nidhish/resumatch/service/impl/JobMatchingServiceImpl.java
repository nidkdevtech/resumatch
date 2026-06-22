package com.nidhish.resumatch.service.impl;

import com.nidhish.resumatch.dto.JobMatchResult;
import com.nidhish.resumatch.model.Job;
import com.nidhish.resumatch.repository.JobEmbeddingRepository;
import com.nidhish.resumatch.repository.JobRepository;
import com.nidhish.resumatch.service.EmbeddingService;
import com.nidhish.resumatch.service.JobMatchingService;
import com.nidhish.resumatch.service.MatchExplanationService;
import com.nidhish.resumatch.service.ResumeParserService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobMatchingServiceImpl implements JobMatchingService {

    private final ResumeParserService  resumeParserService;
    private final EmbeddingService  embeddingService;
    private final JobEmbeddingRepository jobEmbeddingRepository;
    private final JobRepository jobRepository;
    private final MatchExplanationService  matchExplanationService;

    public JobMatchingServiceImpl(ResumeParserService resumeParserService, EmbeddingService embeddingService, JobEmbeddingRepository jobEmbeddingRepository, JobRepository jobRepository, MatchExplanationService matchExplanationService) {
        this.resumeParserService = resumeParserService;
        this.embeddingService = embeddingService;
        this.jobEmbeddingRepository = jobEmbeddingRepository;
        this.jobRepository = jobRepository;
        this.matchExplanationService = matchExplanationService;
    }

    @Override
    public List<JobMatchResult> findMatches(byte[] resumePdfBytes, int topK, String jobType, String experienceLevel) {
        // Step 1: Extract text from PDF
        String resumeText = resumeParserService.extractText(resumePdfBytes);

        // Step 2: Embed the resume text
        float[] resumeEmbedding = embeddingService.embed(resumeText);

        // Step 3: Search pgvector
        String vectorString = toVectorString(resumeEmbedding);
        List<Object[]> rawResults = jobEmbeddingRepository.findSimilarJobEmbeddings(vectorString, topK, jobType, experienceLevel);
        List<Object[]> uniqueJobResults = rawResults.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[1]).longValue(),
                        row -> row,
                        this::keepHigherScore,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();

        // Step 4: Map results to JobMatchResult
        List<JobMatchResult> matches = uniqueJobResults.stream()
                .map(row -> {
                    Long jobId = ((Number) row[1]).longValue();
                    Double similarityScore = ((Number) row[6]).doubleValue();

                    Job job = jobRepository.findById(jobId).orElse(null);

                    if (job == null) return null;

                    return JobMatchResult.builder()
                            .title(job.getTitle())
                            .company(job.getCompany())
                            .location(job.getLocation())
                            .description(job.getDescription())
                            .jobType(job.getJobType())
                            .minSalary(job.getMinSalary())
                            .maxSalary(job.getMaxSalary())
                            .currency(job.getSalaryCurrency())
                            .jobPostingUrl(job.getSourceUrl())
                            .formattedExperienceLevel(job.getExperienceLevel())
                            .similarityScore(similarityScore)
                            .explanation("AI explanation coming soon")
                            .build();
                })
                .filter(result -> result != null)
                .collect(Collectors.toList());

        // Step 5: Add AI explanations
        return matchExplanationService.enrichWithExplanation(resumeText, matches);
    }

    private Object[] keepHigherScore(Object[] current, Object[] candidate) {
        double currentScore = ((Number) current[6]).doubleValue();
        double candidateScore = ((Number) candidate[6]).doubleValue();

        return candidateScore > currentScore ? candidate : current;
    }

    private String toVectorString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
