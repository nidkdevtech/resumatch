package com.nidhish.resumatch.service.impl;

import com.nidhish.resumatch.dto.JobCsvDto;
import com.nidhish.resumatch.model.Job;
import com.nidhish.resumatch.model.JobEmbedding;
import com.nidhish.resumatch.repository.JobEmbeddingRepository;
import com.nidhish.resumatch.repository.JobRepository;
import com.nidhish.resumatch.service.EmbeddingService;
import com.nidhish.resumatch.service.JobIngestionService;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobIngestionServiceImpl implements JobIngestionService {

    private final JobRepository jobRepository;
    private final JobEmbeddingRepository  jobEmbeddingRepository;
    private final EmbeddingService embeddingService;

    public JobIngestionServiceImpl(JobRepository jobRepository, JobEmbeddingRepository jobEmbeddingRepository, EmbeddingService embeddingService) {
        this.jobRepository = jobRepository;
        this.jobEmbeddingRepository = jobEmbeddingRepository;
        this.embeddingService = embeddingService;
    }

    @Override
    public void ingestJobs(String filePath, int limit) {
        try(CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()){
            String[] line;
            int processedRows = 0;
            int ingestedCount = 0;

            System.out.println("CSV File Opened Successfully"); //Debug


            while((line = reader.readNext()) != null && ingestedCount < limit){
                processedRows++;
                System.out.println("Processing row: " + processedRows); //Debug
                // Processing each line here
                JobCsvDto dto = mapToDto(line);

                //Skip if description is empty
                if(dto.getDescription() == null || dto.getTitle() == null) {
                    System.out.println("Skipping row " + processedRows + ": missing title or description");
                    continue;
                }

                //idempotent check - skiip if already exists
                if(jobRepository.existsBySourceUrl(dto.getJobPostingUrl())) {
                    System.out.println("Skipping row " + processedRows + ": source URL already exists");
                    continue;
                }

                // save job tod db
                Job job = mapToEntity(dto);
                Job savedJob = jobRepository.save(job);

                //chunk and embedd and save
                chunkAndEmbed(savedJob);

                ingestedCount++;
                System.out.println("Ingested job "+ingestedCount+": "+dto.getTitle());
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to ingest jobs from CSV: "+ e.getMessage(),e);
        }
    }

    private JobCsvDto mapToDto(String[] line) {
        JobCsvDto dto = new JobCsvDto();
        dto.setTitle(getValueOrNull(line, 2));
        dto.setCompanyName(getValueOrNull(line, 1));
        dto.setDescription(getValueOrNull(line, 3));
        dto.setMaxSalary(getValueOrNull(line, 4));
        dto.setLocation(getValueOrNull(line, 6));
        dto.setMinSalary(getValueOrNull(line, 10));
        dto.setFormattedWorkType(getValueOrNull(line, 11));
        dto.setFormattedExperienceLevel(getValueOrNull(line, 20));
        dto.setJobPostingUrl(getValueOrNull(line, 15));
        dto.setCurrency(getValueOrNull(line, 26));
        dto.setListedTime(getValueOrNull(line, 22));
        return dto;
    }

    private String getValueOrNull(String[] line, int index) {
        try {
            String value = line[index];
            return (value == null || value.trim().isEmpty()) ? null : value.trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private Job mapToEntity(JobCsvDto dto) {
        return Job.builder()
                .title(dto.getTitle())
                .company(dto.getCompanyName())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .jobType(dto.getFormattedWorkType())
                .experienceLevel(dto.getFormattedExperienceLevel())
                .sourceUrl(dto.getJobPostingUrl())
                .salaryCurrency(dto.getCurrency())
                .minSalary(parseSalary(dto.getMinSalary()))
                .maxSalary(parseSalary(dto.getMaxSalary()))
                .postedAt(parseDateTime(dto.getListedTime()))
                .build();
    }

    private Long parseSalary(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return null;
            return (long) Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return null;
            long epochMilli = Long.parseLong(value.trim());
            return LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(epochMilli),
                    java.time.ZoneId.systemDefault()
            );
        } catch (Exception e) {
            return null;
        }
    }

    private void chunkAndEmbed(Job job) {
        String description = job.getDescription();
        if(description == null || description.trim().isEmpty()) return;

        //Split description into chunks
        List<String> chunks = chunkText(description, 500, 50);

        for(int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);

            //Generate embedding for this chunk
            float[] vector = embeddingService.embed(chunk);

            //Save chunk + embedding
            JobEmbedding embedding = JobEmbedding.builder()
                    .job(job)
                    .chunkText(chunk)
                    .chunkIndex((long) i)
                    .embedding(vector)
                    .build();

            jobEmbeddingRepository.save(embedding);
        }
    }

    private List<String> chunkText(String description, int chunkSize, int overlapThreshold) {
        List<String> chunks = new ArrayList<>();

        if (description == null || description.isBlank()) {
            return chunks;
        }

        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be greater than 0");
        }

        if (overlapThreshold < 0 || overlapThreshold >= chunkSize) {
            throw new IllegalArgumentException("overlapThreshold must be >= 0 and < chunkSize");
        }

        int position = 0;

        while(position < description.length()) {
            int end = Math.min(position + chunkSize, description.length());
            String currentChunk = description.substring(position, end);
            chunks.add(currentChunk);

            position += chunkSize - overlapThreshold;
        }
        return chunks;
    }
}
