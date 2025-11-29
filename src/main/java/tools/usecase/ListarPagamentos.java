package tools.usecase;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListarPagamentos {

    private final PagamentoRepository repository;

    public ListarPagamentos(PagamentoRepository repository) {
        this.repository = repository;
    }

    public List<Pagamento> executar() {
        return repository.findAll();
    }
}