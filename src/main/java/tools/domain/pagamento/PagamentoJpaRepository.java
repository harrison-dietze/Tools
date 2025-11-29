package tools.domain.pagamento;

import tools.domain.pagamento.model.Pagamento;
import tools.domain.pagamento.PagamentoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoJpaRepository extends JpaRepository<Pagamento, String>, PagamentoRepository {
}