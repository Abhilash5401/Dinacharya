package com.kanban.util;

import com.kanban.model.entity.Team;
import com.kanban.model.entity.User;
import com.kanban.model.enums.UserRole;
import com.kanban.repository.TeamRepository;
import com.kanban.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Production databases start empty. Import on the Tasks page is hidden until a
 * team exists, so create a default workspace team after the admin user is seeded.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(20)
public class DefaultTeamSeeder implements CommandLineRunner {

    public static final String DEFAULT_TEAM_NAME = "Dinacharya";

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (teamRepository.count() > 0) {
            return;
        }

        User lead = userRepository.findAllDistinct().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .findFirst()
                .orElse(null);
        if (lead == null) {
            log.warn("No admin user yet — skipped default team '{}'", DEFAULT_TEAM_NAME);
            return;
        }

        Team team = Team.builder()
                .name(DEFAULT_TEAM_NAME)
                .description("Default workspace for task imports")
                .lead(lead)
                .members(new HashSet<>())
                .tasks(new HashSet<>())
                .build();
        team.getMembers().add(lead);
        teamRepository.save(team);
        log.info("Created default team '{}' with lead {}", DEFAULT_TEAM_NAME, lead.getEmail());
    }
}
