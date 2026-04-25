package com.wodtracker.userservice.service;

public interface AdminUserEmailService {

    void sendTemporaryPasswordEmail(String email, String name, String temporaryPassword);
}
