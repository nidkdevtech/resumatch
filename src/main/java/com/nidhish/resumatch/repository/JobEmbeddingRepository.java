package com.nidhish.resumatch.repository;

import com.nidhish.resumatch.model.JobEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobEmbeddingRepository extends JpaRepository<JobEmbedding,Long> {
    @Query(value = """
    WITH ranked_embeddings AS (
        SELECT je.id, je.job_id, je.chunk_text, je.chunk_index, je.created_at,
               je.embedding,
               1 - (je.embedding <=> CAST(:embedding AS vector)) AS similarity_score,
               je.embedding <=> CAST(:embedding AS vector) AS distance,
               ROW_NUMBER() OVER (
                   PARTITION BY je.job_id
                   ORDER BY je.embedding <=> CAST(:embedding AS vector)
               ) AS row_number
        FROM job_embeddings je
    )
    SELECT id, job_id, chunk_text, chunk_index, created_at, embedding, similarity_score
    FROM ranked_embeddings
    WHERE row_number = 1
    ORDER BY distance
    LIMIT :topK
    """, nativeQuery = true)

    List<Object[]> findSimilarJobEmbeddings(
            @Param("embedding") String embedding,
            @Param("topK") int topK
    );

    @Query(value = """
    WITH ranked_embeddings AS (
        SELECT je.id, je.job_id, je.chunk_text, je.chunk_index, je.created_at,
               je.embedding,
               1 - (je.embedding <=> CAST(:embedding AS vector)) AS similarity_score,
               je.embedding <=> CAST(:embedding AS vector) AS distance,
               ROW_NUMBER() OVER (
                   PARTITION BY je.job_id
                   ORDER BY je.embedding <=> CAST(:embedding AS vector)
               ) AS row_number
        FROM job_embeddings je
        JOIN jobs j ON j.id = je.job_id
        WHERE (:jobType IS NULL OR :jobType = '' OR LOWER(j.job_type) = LOWER(:jobType))
          AND (:experienceLevel IS NULL OR :experienceLevel = '' OR LOWER(j.experience_level) = LOWER(:experienceLevel))
    )
    SELECT id, job_id, chunk_text, chunk_index, created_at, embedding, similarity_score
    FROM ranked_embeddings
    WHERE row_number = 1
    ORDER BY distance
    LIMIT :topK
    """, nativeQuery = true)
    List<Object[]> findSimilarJobEmbeddings(
            @Param("embedding") String embedding,
            @Param("topK") int topK,
            @Param("jobType") String jobType,
            @Param("experienceLevel") String experienceLevel
    );
    List<JobEmbedding> findByJobId(Long jobId);
}
