package com.codejava.orderapp.controllers;

import com.codejava.orderapp.entities.LoginResponseDTO;
import com.codejava.orderapp.entities.User;
import com.codejava.orderapp.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthenticationController {


    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        return authenticationService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody User user) {
        return authenticationService.loginUser(user.getUsername(), user.getPassword());

    }

    @PutMapping("/update/{username}")
    public ResponseEntity<String> updateUser(@PathVariable String username, @RequestBody User updatedUser) {
        return authenticationService.updateUserDetails(username, updatedUser);
    }


}
