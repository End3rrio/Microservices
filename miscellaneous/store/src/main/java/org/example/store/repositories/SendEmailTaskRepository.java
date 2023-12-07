package org.example.store.repositories;

import org.example.store.entities.SendEmailTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendEmailTaskRepository extends JpaRepository<SendEmailTaskEntity, Long> {
}
