package com.nidhish.resumatch.repository;

import com.nidhish.resumatch.model.Job;
import com.nidhish.resumatch.model.JobEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobEmbeddingRepository extends JpaRepository<JobEmbedding,Long> {
    List<JobEmbedding> findByJobId(Long jobId);
}
