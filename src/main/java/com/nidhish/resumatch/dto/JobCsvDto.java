package com.nidhish.resumatch.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobCsvDto {
    private String title;
    private String companyName;
    private String location;
    private String description;
    private String minSalary;
    private String maxSalary;
    private String currency;
    private String formattedWorkType;
    private String jobPostingUrl;
    private String listedTime;
    private String formattedExperienceLevel;
}
