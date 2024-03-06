package com.codejava.orderapp.services;

import com.codejava.orderapp.entities.LoginResponseDTO;
import com.codejava.orderapp.entities.Role;
import com.codejava.orderapp.entities.User;
import com.codejava.orderapp.repositories.RoleRepository;
import com.codejava.orderapp.repositories.UserRepository;
import exceptions.RegistrationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JWTService tokenService;

    @Autowired
    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                                 JWTService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    // Method to register a new user
    public ResponseEntity<String> registerUser(User user) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                RegistrationException e = new RegistrationException("Username already exists", HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(e.getStatusCode()).body("Username already exists");
            } else {
                // Check if password is null or empty
                if (user.getPassword() == null || user.getPassword().equals("")) {
                    RegistrationException e = new RegistrationException("Password cannot be null", HttpStatus.BAD_REQUEST);
                    return ResponseEntity.status(e.getStatusCode()).body("Password cannot be null");
                }
                // Encode password
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                // Retrieve user role
                Role userRole = roleRepository.findByAuthority("ROLE_USER").orElseThrow(() -> new RegistrationException("Could not find user role", HttpStatus.INTERNAL_SERVER_ERROR));
                Set<Role> authorities = new HashSet<>();
                authorities.add(userRole);
                // Set encoded password and user role
                user.setPassword(encodedPassword);
                user.setAuthorities(authorities);
                User registeredUser = userRepository.save(user);
                return ResponseEntity.ok(registeredUser.toString());
            }
        } catch (RegistrationException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Method to authenticate and login a user
    public ResponseEntity<LoginResponseDTO> loginUser(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            Optional<User> user = userRepository.findByUsername(username);
            // If user exists, generate JWT token and return login response
            if (user.isPresent()) {
                String token = tokenService.generateToken(username);
                return ResponseEntity.ok(new LoginResponseDTO(user.get(), token));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LoginResponseDTO(null, "User not found"));
            }

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO(null, "Authentication failed->user not found"));
        }
    }


    // Method to update user details
    public ResponseEntity<String> updateUserDetails(String username, User updatedUser) {
        try {
            Optional<User> existingUserOptional = userRepository.findByUsername(username);

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                // Update user details
                existingUser.setAddress(updatedUser.getAddress());

                User savedUser = userRepository.save(existingUser);

                return ResponseEntity.ok(savedUser.toString());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
