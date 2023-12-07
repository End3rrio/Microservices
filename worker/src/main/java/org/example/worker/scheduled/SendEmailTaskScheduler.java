package org.example.worker.scheduled;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.example.store.dao.SendEmailTaskDao;
import org.example.worker.Application;
import org.example.worker.service.EmailClientApi;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SendEmailTaskScheduler {

    SendEmailTaskDao sendEmailTaskDao;

    EmailClientApi emailClientApi;



    @Scheduled(cron = "*/5 * * * * *")
    public void executeSendEmailTasks() {
        sendEmailTaskDao
                .findAllNotProcessed()
                .forEach(sendEmailTask -> {

                    String destinationEmail = sendEmailTask.getDestinationEmail();
                    String message = sendEmailTask.getMessage();

                    boolean delivered = emailClientApi.sendEmail(destinationEmail, message);

                    if (delivered) {

                        log.debug("Task %d already processed. ".formatted(sendEmailTask.getId()));
                        sendEmailTaskDao.markAsProcessed(sendEmailTask);

                        return;
                    }

                    log.warn("Task %d returned to process. ".formatted(sendEmailTask.getId()));
                    sendEmailTaskDao.updateLatestTryAt(sendEmailTask);
                });
    }
}
