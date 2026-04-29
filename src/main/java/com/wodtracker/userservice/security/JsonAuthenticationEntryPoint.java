package com.wodtracker.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wodtracker.userservice.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        writeErrorResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                "No autorizado",
                "Debes autenticarte para acceder a este recurso"
        );
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String error,
            String message
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
                response.getWriter(),
                new ErrorResponse(OffsetDateTime.now(), status.value(), error, message, null)
        );
    }
}
