package com.nidhish.resumatch.service.impl;

import com.nidhish.resumatch.dto.JobMatchResult;
import com.nidhish.resumatch.service.MatchExplanationService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MatchExplanationServiceImpl implements MatchExplanationService {
    private final ChatLanguageModel  chatLanguageModel;
    Pattern pattern = Pattern.compile("^(\\d+)\\.\\s*(.+)$");

    public MatchExplanationServiceImpl(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @Override
    public List<JobMatchResult> enrichWithExplanation(String resumeText, List<JobMatchResult> matches) {
        List<List<JobMatchResult>> batches = partition(matches, 5);

        for(List<JobMatchResult> batch : batches) {
            String prompt = buildPrompt(resumeText, batch);
            String response = chatLanguageModel.generate(prompt);
            parseAndEnrich(response, batch);
        }

        return matches;
    }

    private List<List<JobMatchResult>> partition(List<JobMatchResult> list, int batchSize) {
        // split list into sublists of batchSize
        List<List<JobMatchResult>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }
    private String buildPrompt(String resumeText, List<JobMatchResult> batch) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are explaining job matches for a resume screening app.\n")
                .append("For each numbered job, write exactly one concise sentence explaining why it matches the resume.\n")
                .append("Return only numbered lines in the format '1. explanation'. Do not include extra text.\n\n")
                .append("Resume:\n")
                .append(truncate(resumeText, 6000))
                .append("\n\nJobs:\n");

        for (int i = 0; i < batch.size(); i++) {
            JobMatchResult job = batch.get(i);
            prompt.append(i + 1)
                    .append(". Title: ").append(nullToBlank(job.getTitle()))
                    .append("\n   Company: ").append(nullToBlank(job.getCompany()))
                    .append("\n   Location: ").append(nullToBlank(job.getLocation()))
                    .append("\n   Experience: ").append(nullToBlank(job.getFormattedExperienceLevel()))
                    .append("\n   Type: ").append(nullToBlank(job.getJobType()))
                    .append("\n   Description: ").append(truncate(job.getDescription(), 1200))
                    .append("\n");
        }

        return prompt.toString();
    }

    private void parseAndEnrich(String response, List<JobMatchResult> batch) {
        String[] lines =  response.split("\n");

        for(String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());

            if (matcher.matches()) {
                int index = Integer.parseInt(matcher.group(1)) - 1;

                if (index >= 0 && index < batch.size()) {
                    batch.get(index).setExplanation(matcher.group(2).trim());
                }
            }
        }
    }

    public String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    private  String nullToBlank(String s) {
        return s == null ? "" : s;
    }

}
