package com.nidhish.resumatch.repository;

import com.nidhish.resumatch.model.Job;
import com.nidhish.resumatch.model.JobEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobEmbeddingRepository extends JpaRepository<JobEmbedding,Long> {
    @Query(value = """
    SELECT je.id, je.job_id, je.chunk_text, je.chunk_index, je.created_at,
           je.embedding,
           1 - (je.embedding <=> CAST(:embedding AS vector)) AS similarity_score
    FROM job_embeddings je
    ORDER BY je.embedding <=> CAST(:embedding AS vector)
    LIMIT :topK
    """, nativeQuery = true)

    List<Object[]> findSimilarJobEmbeddings(
            @Param("embedding") String embedding,
            @Param("topK") int topK
    );
    List<JobEmbedding> findByJobId(Long jobId);
}
