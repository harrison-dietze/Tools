package tools.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tools.domain.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
