package com.codejava.orderapp.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private User user;
    private String jwt;

    @Override
    public String toString() {
        return "User {" + user.getUsername() + user.getPassword() + "}, jwt='" + jwt + '\'' +
                '}';
    }
}