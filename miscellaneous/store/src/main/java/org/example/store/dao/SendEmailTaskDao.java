package org.example.store.dao;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.store.entities.SendEmailTaskEntity;
import org.example.store.repositories.SendEmailTaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendEmailTaskDao {

    SendEmailTaskRepository sendEmailTaskRepository;

    @Transactional
    public SendEmailTaskEntity save(SendEmailTaskEntity entity) {
        return sendEmailTaskRepository.save(entity);
    }

    @Transactional
    public List<SendEmailTaskEntity> findAllNotProcessed() {
        return  sendEmailTaskRepository.findAllNotProcessed();
    }
    @Transactional
    public void markAsProcessed(SendEmailTaskEntity sendEmailTask) {
        sendEmailTaskRepository.markAsProcessed(sendEmailTask.getId());
    }

    public void updateLatestTryAt(SendEmailTaskEntity entity) {
        sendEmailTaskRepository.updateLatestTryAt(entity.getId());
    }
}
