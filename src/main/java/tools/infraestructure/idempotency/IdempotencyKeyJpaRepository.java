package tools.infrastructure.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IdempotencyKeyJpaRepository extends JpaRepository<IdempotencyKey, String>, IdempotencyKeyRepository {
    Optional<IdempotencyKey> findById(String keyId);
}