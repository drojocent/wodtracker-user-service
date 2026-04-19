package com.wodtracker.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootTest
class WodtrackerUserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodShouldStartApplication() {
        try (var context = new SpringApplicationBuilder(WodtrackerUserServiceApplication.class)
                .profiles("test")
                .properties("server.port=0")
                .run()) {
            // Context starts successfully using the application entry configuration.
        }
    }
}
