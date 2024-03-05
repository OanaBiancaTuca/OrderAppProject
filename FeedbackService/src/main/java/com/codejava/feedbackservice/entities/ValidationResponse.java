package com.codejava.feedbackservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "validations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String typeOfValidation;
    private String response;
}
