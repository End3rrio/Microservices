package org.example.worker.scheduled;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.example.store.dao.SendEmailTaskDao;
import org.example.store.entities.SendEmailTaskEntity;
import org.example.worker.service.EmailClientApi;
import org.example.worker.service.RedisLock;
import org.example.worker.service.RedisLockWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendEmailTaskScheduler {

    SendEmailTaskDao sendEmailTaskDao;

    RedisLock redisLock;

    EmailClientApi emailClientApi;

    RedisLockWrapper redisLockWrapper;

    private static final String SEND_EMAIL_TASK_KEY_FORMAT = ":send:email:task:%s";

    @Scheduled(cron = "*/5 * * * * *")
    public void executeSendEmailTasks() {

        log.debug("Worker start execution.");

        List<Long> sendEmailTaskIds = sendEmailTaskDao.findNotProcessedIds();

        for (Long sendEmailTaskId: sendEmailTaskIds) {

            String sendEmailTaskKey = getSendEmailTaskKey(sendEmailTaskId);

            redisLockWrapper.lockAndExecuteTask(
                    sendEmailTaskKey,
                    Duration.ofSeconds(5),
                    () -> sendEmail(sendEmailTaskId)
            );
        }
    }

    private void sendEmail(Long sendEmailTaskId) {

        Optional<SendEmailTaskEntity> optionalSendEmailTask = sendEmailTaskDao
                .findNotProcessedById(sendEmailTaskId);

        if (optionalSendEmailTask.isEmpty()) {
            log.info("Task %d already in process.".formatted(sendEmailTaskId));
            return;
        }

        SendEmailTaskEntity sendEmailTask = optionalSendEmailTask.get();

        String destinationEmail = sendEmailTask.getDestinationEmail();
        String message = sendEmailTask.getMessage();

        boolean delivered = emailClientApi.sendEmail(destinationEmail, message);

        if (delivered) {

            log.debug("Task %d processed.".formatted(sendEmailTask.getId()));
            sendEmailTaskDao.markAsProcessed(sendEmailTask);

            return;
        }

        log.warn("Task %d returned to process.".formatted(sendEmailTask.getId()));
        sendEmailTaskDao.updateLatestTryAt(sendEmailTask);
    }

    private static String getSendEmailTaskKey(Long taskId) {
        return SEND_EMAIL_TASK_KEY_FORMAT.formatted(taskId);
    }
}
