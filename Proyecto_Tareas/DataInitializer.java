package com.taskmanager.config;

import com.taskmanager.model.Role;
import com.taskmanager.model.User;
import com.taskmanager.repository.RoleRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName(Role.RoleName.ROLE_USER);
            roleRepository.save(userRole);
            
            Role adminRole = new Role();
            adminRole.setName(Role.RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
            
            System.out.println("Roles creados exitosamente");
        }
        
        // Crear usuarios de prueba si no existen
        if (userRepository.count() == 0) {
            // Usuario ADMIN
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@taskmanager.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado")));
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            
            // Usuario USER
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@taskmanager.com");
            user.setPassword(passwordEncoder.encode("user123"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleRepository.findByName(Role.RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Rol USER no encontrado")));
            user.setRoles(userRoles);
            userRepository.save(user);
            
            System.out.println("Usuarios de prueba creados:");
            System.out.println("ADMIN - username: admin, password: admin123");
            System.out.println("USER - username: user, password: user123");
        }
    }
}
