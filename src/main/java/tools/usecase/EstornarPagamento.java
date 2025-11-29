package tools.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;
import tools.domain.pagamento.model.StatusPagamento;
import tools.exception.*;

@Service
public class EstornarPagamento {

    private final PagamentoRepository repository;

    public EstornarPagamento(PagamentoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pagamento executar(String id) {

        Pagamento pagamento = repository.findById(id).orElseThrow(() -> new MappedExceptions.PagamentoNaoEncontradoException("Pagamento não encontrado para estorno com ID: " + id));

        if (pagamento.getStatus() == StatusPagamento.CANCELADO) {
            throw new MappedExceptions.PagamentoJaCanceladoException("O pagamento ID " + id + " já está cancelado.");
        }

        pagamento.setStatus(StatusPagamento.CANCELADO);

        return repository.save(pagamento);
    }

}
