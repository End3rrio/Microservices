package org.example.store.dao;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.store.entities.SendEmailTaskEntity;
import org.example.store.repositories.SendEmailTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendEmailTaskDao {

    SendEmailTaskRepository sendEmailTaskRepository;

    private static final Duration TASK_EXECUTE_DURATION = Duration.ofSeconds(10);


    @Transactional
    public SendEmailTaskEntity save(SendEmailTaskEntity entity) {
        return sendEmailTaskRepository.save(entity);
    }

    public List<Long> findNotProcessedIds() {

        Instant latestTryAtLte = Instant.now().minus(TASK_EXECUTE_DURATION);

        return sendEmailTaskRepository.findAllNotProcessed(latestTryAtLte);
    }

    public Optional<SendEmailTaskEntity> findNotProcessedById(Long id) {

        Instant latestTryAtLte = Instant.now().minus(TASK_EXECUTE_DURATION);

        return sendEmailTaskRepository.findNotProcessedById(id, latestTryAtLte);
    }
    @Transactional
    public void markAsProcessed(SendEmailTaskEntity sendEmailTask) {
        sendEmailTaskRepository.markAsProcessed(sendEmailTask.getId());
    }

    public void updateLatestTryAt(SendEmailTaskEntity entity) {
        sendEmailTaskRepository.updateLatestTryAt(entity.getId());
    }
}
