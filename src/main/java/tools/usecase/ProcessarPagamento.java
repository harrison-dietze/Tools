package tools.usecase;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;
import tools.domain.pagamento.model.StatusPagamento;
import tools.domain.pagamento.model.TipoPagamento;

import tools.infrastructure.idempotency.IdempotencyKey;
import tools.infrastructure.idempotency.IdempotencyKeyRepository;

import tools.exception.MappedExceptions;
import tools.exception.MappedExceptions.PagamentoJaCanceladoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProcessarPagamento {

    private final PagamentoRepository pagamentoRepository;

    private final IdempotencyKeyRepository idempotencyRepository;

    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(ProcessarPagamento.class);

    public ProcessarPagamento(
            PagamentoRepository pagamentoRepository,
            IdempotencyKeyRepository idempotencyRepository,
            ObjectMapper objectMapper) {
        this.pagamentoRepository = pagamentoRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Pagamento executar(Pagamento pagamento, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            Optional<IdempotencyKey> existingKey = idempotencyRepository.findById(idempotencyKey);
            if (existingKey.isPresent()) {
                log.warn("Tentativa de processar pagamento com chave de idempotência duplicada: {}", idempotencyKey); // LOGGED

                String payload = existingKey.get().getResponsePayload();
                throw new MappedExceptions.IdempotencyConflictException("Requisição duplicada com a mesma chave de idempotência.", payload);
            }
        }

        validarRegrasDeParcelamento(pagamento);

        StatusPagamento status = simularAutorizacao(pagamento);
        pagamento.setStatus(status);

        if (status == StatusPagamento.AUTORIZADO) {
            if (pagamento.getId() == null || pagamento.getId().isBlank()) {
                pagamento.setId(UUID.randomUUID().toString());
            }
            pagamento.getDescricao().setNsu(gerarNsu());
            pagamento.getDescricao().setCodigoAutorizacao(gerarCodigoAutorizacao());
        } else {
            pagamento.getDescricao().setNsu(null);
            pagamento.getDescricao().setCodigoAutorizacao(null);
        }

        Pagamento pagamentoProcessado = pagamentoRepository.save(pagamento);

        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            salvarChaveIdempotente(idempotencyKey, pagamentoProcessado);
        }

        return pagamentoProcessado;
    }

    private void validarRegrasDeParcelamento(Pagamento pagamento) {
        TipoPagamento tipo = pagamento.getFormaPagamento().getTipo();
        int parcelas = pagamento.getFormaPagamento().getParcelas();

        if (tipo == TipoPagamento.AVISTA && parcelas != 1) {
            throw new PagamentoJaCanceladoException("Pagamento à vista deve ter exatamente 1 parcela.");
        }
        if ((tipo == TipoPagamento.PARCELADO_LOJA || tipo == TipoPagamento.PARCELADO_EMISSOR) && parcelas <= 1) {
            throw new PagamentoJaCanceladoException("Pagamento parcelado deve ter mais de 1 parcela.");
        }
    }

    private StatusPagamento simularAutorizacao(Pagamento pagamento) {
        return StatusPagamento.AUTORIZADO;
    }

    private String gerarNsu() {
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }

    private String gerarCodigoAutorizacao() {
        return String.valueOf(UUID.randomUUID().getMostSignificantBits()).substring(1, 10);
    }

    private void salvarChaveIdempotente(String keyId, Pagamento resposta) {
        try {
            IdempotencyKey newKey = new IdempotencyKey();
            newKey.setKeyId(keyId);
            newKey.setCreatedAt(LocalDateTime.now());
            newKey.setResponsePayload(objectMapper.writeValueAsString(resposta));
            newKey.setHttpStatus(201);
            idempotencyRepository.save(newKey);
        } catch (Exception e) {
            log.error("Erro ao salvar chave de idempotência {}: {}", keyId, e.getMessage(), e);
        }
    }
}