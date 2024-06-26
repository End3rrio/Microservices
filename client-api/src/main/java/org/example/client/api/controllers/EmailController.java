package org.example.client.api.controllers;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.store.dao.SendEmailTaskDao;
import org.example.store.entities.SendEmailTaskEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class EmailController {

    SendEmailTaskDao sendEmailTaskDao;

    public static final String SEND_EMAIL = "/api/email/send";

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(SEND_EMAIL)
    public void sendEmail(
            @RequestParam("destination_email") String destinationEmail,
            @RequestParam String message)  {

        sendEmailTaskDao.save(
    SendEmailTaskEntity.builder()
            .destinationEmail(destinationEmail)
            .message(message)
            .build()
        );
    }
}