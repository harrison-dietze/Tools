package tools.infrastructure.idempotency;

import java.util.Optional;
import java.util.List;

public interface IdempotencyKeyRepository {
    Optional<IdempotencyKey> findById(String id);
    List<IdempotencyKey> findAll();
    IdempotencyKey save(IdempotencyKey pagamento);
    boolean existsById(String id);
}