package com.wodtracker.userservice.service.impl;

import com.wodtracker.userservice.exception.EmailDeliveryException;
import com.wodtracker.userservice.service.AdminUserEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailAdminUserEmailService implements AdminUserEmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailAdminUserEmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendTemporaryPasswordEmail(String email, String name, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Tu cuenta de WODTracker ha sido creada");
        message.setText(buildBody(name, temporaryPassword));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new EmailDeliveryException("No se pudo completar la operación porque no fue posible enviar la notificacion", ex);
        }
    }

    private String buildBody(String name, String temporaryPassword) {
        return """
                Hola %s,

                ¡Bienvenido a WODTracker!

                Tu cuenta ha sido creada correctamente.
                Ya puedes acceder a la aplicación utilizando la siguiente contraseña temporal: %s
                
                Por motivos de seguridad, te recomendamos cambiar esta contraseña tras tu primer inicio de sesión.

                ¡Nos vemos en el box!
                Equipo WODTracker
                """.formatted(name, temporaryPassword);
    }
}
