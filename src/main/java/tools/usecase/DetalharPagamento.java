package tools.usecase;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class DetalharPagamento {

    private final PagamentoRepository repository;

    public DetalharPagamento(PagamentoRepository repository) {
        this.repository = repository;
    }

    public Optional<Pagamento> executar(String id) {
        return repository.findById(id);
    }
}