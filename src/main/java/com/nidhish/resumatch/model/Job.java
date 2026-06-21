package com.nidhish.resumatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String company;
    private String location;
    @Column(name = "job_type")
    private String jobType;

    @Column(name = "min_salary")
    private Long minSalary;
    @Column(name = "max_salary")
    private Long maxSalary;

    @Column(name = "salary_currency")
    private String salaryCurrency;
    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}