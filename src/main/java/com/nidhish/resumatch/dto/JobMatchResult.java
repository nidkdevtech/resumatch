package com.nidhish.resumatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobMatchResult {
    private String title;
    private String company;
    private String location;
    private String description;
    private Long minSalary;
    private Long maxSalary;
    private String currency;
    private String jobType;
    private String jobPostingUrl;
    private LocalDateTime listedTime;
    private String formattedExperienceLevel;
    private Double similarityScore;
    private String explanation;
}
