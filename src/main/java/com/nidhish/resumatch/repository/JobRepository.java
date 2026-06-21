package com.nidhish.resumatch.repository;

import com.nidhish.resumatch.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long>{
    boolean existsBySourceUrl(String sourceUrl);
}
