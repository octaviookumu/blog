package com.octaviookumu.blog.config;

import com.octaviookumu.blog.repositories.UserRepository;
import com.octaviookumu.blog.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.admin.name:Admin}")
    private String adminName;

    /**
     * CommandLineRunner is a Spring Boot interface — any bean implementing it gets its run()
     * method called once, automatically, right after the app finishes starting up.
     * <p>
     * That's the only "trigger" here; nothing else calls this class.
     * </p>
     *
     * @param args arguments
     */
    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.info("ADMIN_EMAIL/ADMIN_PASSWORD not set - skipping admin seed");
            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin user {} already exists - skipping admin seed", adminEmail);
            return;
        }

        userService.createUser(adminName, adminEmail, adminPassword);
        log.info("Created admin user {}", adminEmail);

    }
}
