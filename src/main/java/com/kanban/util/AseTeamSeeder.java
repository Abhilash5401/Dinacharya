package com.kanban.util;

import com.kanban.model.entity.Department;
import com.kanban.model.entity.User;
import com.kanban.model.enums.EmployeeStatus;
import com.kanban.model.enums.EmploymentType;
import com.kanban.model.enums.UserRole;
import com.kanban.repository.DepartmentRepository;
import com.kanban.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(20)
public class AseTeamSeeder implements ApplicationRunner {

    public static final String DEPARTMENT = "ASE";
    public static final String ROLE = "ASE";
    public static final String DEFAULT_PASSWORD = "Welcome@1234";

    public static final List<String> ASE_MEMBERS = List.of(
        "Akkipalli Sri Usha",
        "CH Nikhileshwar Reddy",
        "Ajay Kumar Ramavath",
        "Chintala siva Subramanyam",
        "Nanneboina Hemanth kumar",
        "Kota Prasanthi",
        "Ananya Kamboja",
        "Dondapati Jyothsna Amisha",
        "Boojala Sai Vignesh Reddy",
        "Pattima kalyani"
    );

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!departmentRepository.existsByNameIgnoreCase(DEPARTMENT)) {
            departmentRepository.save(Department.builder().name(DEPARTMENT).build());
            log.info("Seeded department: {}", DEPARTMENT);
        }

        for (String name : ASE_MEMBERS) {
            if (userRepository.findByNameIgnoreCase(name).isPresent()) {
                continue;
            }
            String email = emailFor(name);
            if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                continue;
            }

            User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .department(DEPARTMENT)
                .professionalRole(ROLE)
                .role(UserRole.MEMBER)
                .employeeStatus(EmployeeStatus.ACTIVE)
                .employmentType(EmploymentType.FULL_TIME)
                .isActive(true)
                .build();
            userRepository.save(user);
            log.info("Seeded ASE member '{}' ({})", name, email);
        }
    }

    private static String emailFor(String name) {
        String slug = name.toLowerCase()
            .replaceAll("[^a-z0-9]+", ".")
            .replaceAll("^\\.|\\.$", "");
        return slug + "@imported.local";
    }
}
