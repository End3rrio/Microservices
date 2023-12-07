package org.example.store.repositories;

import org.example.store.entities.SendEmailTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SendEmailTaskRepository extends JpaRepository<SendEmailTaskEntity, Long> {
    @Query("""
            SELECT task 
            FROM SendEmailTaskEntity task
            WHERE task.processedAt IS NULL 
            ORDER BY task.createdAt
    """)
    List<SendEmailTaskEntity> findAllNotProcessed();

    @Modifying
    @Query("""
            UPDATE SendEmailTaskEntity task
            SET task.processedAt = NOW()
            WHERE task.id = :id
    """)
    void markAsProcessed(Long id);

    @Modifying
    @Query("""
    UPDATE SendEmailTaskEntity task
    SET task.latestTryAt = NOW()
    WHERE task.id = :id
    """)
    void updateLatestTryAt(Long id);
}
