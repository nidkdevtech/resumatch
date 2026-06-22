package com.nidhish.resumatch.controller;

import com.nidhish.resumatch.dto.JobMatchResult;
import com.nidhish.resumatch.service.JobMatchingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class JobMatchingController {

    private final JobMatchingService jobMatchingService;

    public JobMatchingController(JobMatchingService jobMatchingService) {
        this.jobMatchingService = jobMatchingService;
    }

    @PostMapping("/match")
    public List<JobMatchResult> match(@RequestParam("file") MultipartFile pdf,
                                      @RequestParam(defaultValue = "20") int topK,
                                      @RequestParam(required = false) String jobType,
                                      @RequestParam(required = false) String experienceLevel){
        try {
            byte[] bytes  = pdf.getBytes();
            return jobMatchingService.findMatches(bytes, topK, jobType, experienceLevel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file: "+e.getMessage(),e);
        }
    }
}
