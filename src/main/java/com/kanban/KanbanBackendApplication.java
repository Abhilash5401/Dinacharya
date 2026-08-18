package com.kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KanbanBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbanBackendApplication.class, args);
    }
}
