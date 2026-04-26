package com.wodtracker.userservice.service.impl;

import com.wodtracker.userservice.exception.EmailDeliveryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailAdminUserEmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void shouldSendTemporaryPasswordEmail() {
        MailAdminUserEmailService service = new MailAdminUserEmailService(javaMailSender, "noreply@wodtracker.local");

        service.sendTemporaryPasswordEmail("athlete@example.com", "Athlete", "TempPass123!");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo("noreply@wodtracker.local");
        assertThat(sentMessage.getTo()).containsExactly("athlete@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Tu cuenta de WODTracker ha sido creada");
        assertThat(sentMessage.getText()).contains("Athlete");
        assertThat(sentMessage.getText()).contains("TempPass123!");
        assertThat(sentMessage.getText()).contains("te recomendamos cambiar esta contraseña tras tu primer inicio de sesión");
    }

    @Test
    void shouldWrapMailFailures() {
        MailAdminUserEmailService service = new MailAdminUserEmailService(javaMailSender, "noreply@wodtracker.local");
        doThrow(new MailSendException("smtp down")).when(javaMailSender).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));

        assertThatThrownBy(() -> service.sendTemporaryPasswordEmail("athlete@example.com", "Athlete", "TempPass123!"))
                .isInstanceOf(EmailDeliveryException.class)
                .hasMessage("No se pudo completar la operación porque no fue posible enviar la notificacion");
    }
}
