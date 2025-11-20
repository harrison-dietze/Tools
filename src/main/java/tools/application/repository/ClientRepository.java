package tools.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tools.domain.model.Client;

public interface ClientRepository extends JpaRepository<User, Long> {
}
