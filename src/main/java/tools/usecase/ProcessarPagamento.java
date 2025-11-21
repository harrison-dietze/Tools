package tools.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;
import tools.domain.pagamento.model.StatusPagamento;

@Service
public class ProcessarPagamento {
	
	private final PagamentoRepository repository;

    public ProcessarPagamento(PagamentoRepository repository) {
        this.repository = repository;
    }
	
    @Transactional
    public Pagamento executar(Pagamento pagamento) {
        if (pagamento.getId() == null || repository.existsById(pagamento.getId())) {
            pagamento.setId(UUID.randomUUID().toString());
        }
        
        pagamento.setStatus(StatusPagamento.AUTORIZADO); 

        pagamento.getDescricao().setNsu(gerarNsu());
        pagamento.getDescricao().setCodigoAutorizacao(gerarCodigoAutorizacao());

        return repository.save(pagamento);
    }
    
    private String gerarNsu() {
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }
    
    private String gerarCodigoAutorizacao() {
        return String.valueOf(UUID.randomUUID().getMostSignificantBits()).substring(1, 10);
    }
}
