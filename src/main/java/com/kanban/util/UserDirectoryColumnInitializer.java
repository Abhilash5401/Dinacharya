package com.kanban.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class UserDirectoryColumnInitializer implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            addColumn(statement, "professional_role", "VARCHAR(255) NULL");
            addColumn(statement, "github_profile", "VARCHAR(255) NULL");
            addColumn(statement, "employee_status", "VARCHAR(32) NULL");
        } catch (Exception ex) {
            log.warn("Could not ensure directory columns exist: {}", ex.getMessage());
        }
    }

    private void addColumn(Statement statement, String column, String definition) {
        try {
            statement.execute("ALTER TABLE users ADD COLUMN " + column + " " + definition);
            log.info("Added users.{}", column);
        } catch (Exception ex) {
            String message = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
            if (!message.contains("duplicate")) {
                log.debug("users.{} already present or could not be added: {}", column, ex.getMessage());
            }
        }
    }
}
