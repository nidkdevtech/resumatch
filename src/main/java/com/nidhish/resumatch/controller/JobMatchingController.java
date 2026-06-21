package com.nidhish.resumatch.controller;

import com.nidhish.resumatch.dto.JobMatchResult;
import com.nidhish.resumatch.service.JobMatchingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class JobMatchingController {

    private final JobMatchingService jobMatchingService;

    public JobMatchingController(JobMatchingService jobMatchingService) {
        this.jobMatchingService = jobMatchingService;
    }

    @PostMapping("/match")
    public List<JobMatchResult> match(@RequestParam("file") MultipartFile pdf){
        try {
            byte[] bytes  = pdf.getBytes();
            return jobMatchingService.findMatches(bytes, 20);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file: "+e.getMessage(),e);
        }
    }
}
