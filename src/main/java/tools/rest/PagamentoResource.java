package tools.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tools.usecase.ProcessarPagamento;
import tools.usecase.EstornarPagamento;
import tools.usecase.ListarPagamentos;
import tools.usecase.DetalharPagamento;
import tools.domain.pagamento.model.Pagamento;
import tools.exception.MappedExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoResource {

    private final ProcessarPagamento processarPagamento;
    private final EstornarPagamento estornarPagamento;
    private final DetalharPagamento detalharPagamento;
    private final ListarPagamentos listarPagamentos;
    private final ObjectMapper objectMapper;

    public PagamentoResource(
            ProcessarPagamento processarPagamento,
            EstornarPagamento estornarPagamento,
            DetalharPagamento detalharPagamento,
            ListarPagamentos listarPagamentos,
            ObjectMapper objectMapper
    ) {
        this.processarPagamento = processarPagamento;
        this.estornarPagamento = estornarPagamento;
        this.detalharPagamento = detalharPagamento;
        this.listarPagamentos = listarPagamentos;
        this.objectMapper = objectMapper;
    }

    @PostMapping()
    public ResponseEntity<Map<String, ?>> processarPagamento(
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody Map<String, Pagamento> request) {

        Pagamento pagamentoEntrada = request.get("pagamento");

        if (pagamentoEntrada == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Corpo da requisição inválido. Objeto 'pagamento' ausente."));
        }

        try {
            Pagamento pagamentoProcessado = processarPagamento.executar(pagamentoEntrada, idempotencyKey);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("pagamento", pagamentoProcessado));
        } catch (MappedExceptions.IdempotencyConflictException e) {
            try {
                Map<String, ?> savedResponse = objectMapper.readValue(
                        e.getResponsePayload(),
                        new TypeReference<Map<String, ?>>() {}
                );

                return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
            } catch (JsonProcessingException jsonEx) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Erro interno ao desserializar resposta de idempotência."));
            }
        }
    }

    @PostMapping("/{id}/estorno")
    public ResponseEntity<Map<String, Pagamento>> estornar(@PathVariable String id) {
        Pagamento pagamentoEstornado = estornarPagamento.executar(id);

        return ResponseEntity.ok(Collections.singletonMap("pagamento", pagamentoEstornado));
    }

    @GetMapping()
    public ResponseEntity<List<Pagamento>> listarTodosPagamentos() {
        return ResponseEntity.ok(listarPagamentos.executar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Pagamento>> detalharPagamento(@PathVariable String id) {
        Optional<Pagamento> pagamento = detalharPagamento.executar(id);

        return pagamento
                .map(p -> ResponseEntity.ok(Collections.singletonMap("pagamento", p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}