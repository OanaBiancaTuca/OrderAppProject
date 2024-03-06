package com.codejava.orderapp.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    private String username;
    private String password;

    public String toString() {
        return "Registration info: username: " + this.username + " password: " + this.password;
    }
}
