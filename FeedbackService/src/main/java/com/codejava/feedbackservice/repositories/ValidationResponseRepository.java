package com.codejava.feedbackservice.repositories;

import com.codejava.feedbackservice.entities.ValidationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValidationResponseRepository extends JpaRepository<ValidationResponse,Long> {
    List<ValidationResponse> findByOrderId(Long orderId);
}
