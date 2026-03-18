package com.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            // Check if admin already exists
            if (repo.findByEmail("admin@railnova.com").isEmpty()) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@railnova.com");
                admin.setPassword(encoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);

                repo.save(admin);

                System.out.println("✅ Default admin created: admin@railnova.com / Admin@123");
            } else {
                System.out.println("ℹ️ Admin already exists in database.");
            }
        };
    }
}