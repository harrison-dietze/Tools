package tools.domain.pagamento;

import java.util.List;
import java.util.Optional;

import tools.domain.pagamento.model.Pagamento;

public interface PagamentoRepository {
    Optional<Pagamento> findById(String id);
    List<Pagamento> findAll();
    Pagamento save(Pagamento pagamento);
    boolean existsById(String id);
}
