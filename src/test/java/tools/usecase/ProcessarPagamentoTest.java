package tools.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;
import tools.domain.pagamento.model.DescricaoPagamento; // Necessário para inicialização
import tools.domain.pagamento.model.FormaPagamento;
import tools.domain.pagamento.model.StatusPagamento;
import tools.domain.pagamento.model.TipoPagamento;
import tools.exception.MappedExceptions;
import tools.infrastructure.idempotency.IdempotencyKey;
import tools.infrastructure.idempotency.IdempotencyKeyRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarPagamentoTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProcessarPagamento processarPagamento;

    private Pagamento pagamentoValido;
    private static final String IDEMPOTENCY_KEY = "test-uuid";

    @BeforeEach
    void setUp() {
        pagamentoValido = new Pagamento();
        pagamentoValido.setId(null);
        pagamentoValido.setCartao("1234");

        pagamentoValido.setDescricao(new DescricaoPagamento());

        pagamentoValido.setFormaPagamento(new FormaPagamento(TipoPagamento.AVISTA, 1));
    }

    @Test
    void deveProcessarPagamentoComSucessoQuandoChaveNaoExistir() throws JsonProcessingException {
        when(idempotencyRepository.findById(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoValido);

        Pagamento resultado = processarPagamento.executar(pagamentoValido, IDEMPOTENCY_KEY);

        assertNotNull(resultado.getStatus());
        assertEquals(StatusPagamento.AUTORIZADO, resultado.getStatus());
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
        verify(idempotencyRepository, times(1)).save(any(IdempotencyKey.class));
    }

    @Test
    void deveGerarNovoIdQuandoIdNuloOuExistente() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoValido);

        pagamentoValido.setId(null);
        processarPagamento.executar(pagamentoValido, null);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

        String idExistente = UUID.randomUUID().toString();
        pagamentoValido.setId(idExistente);

        processarPagamento.executar(pagamentoValido, null);
        verify(pagamentoRepository, times(2)).save(any(Pagamento.class));
    }

    @Test
    void devePassarQuandoAVistaTiverUmaParcela() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoValido);

        assertDoesNotThrow(() -> processarPagamento.executar(pagamentoValido, null));
    }

    @Test
    void devePassarQuandoParceladoTiverMaisDeUmaParcela() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamentoValido);

        pagamentoValido.setFormaPagamento(new FormaPagamento(TipoPagamento.PARCELADO_LOJA, 3));
        assertDoesNotThrow(() -> processarPagamento.executar(pagamentoValido, null));
    }

    @Test
    void deveLancarExcecaoQuandoAVistaTiverMaisDeUmaParcela() {
        pagamentoValido.setFormaPagamento(new FormaPagamento(TipoPagamento.AVISTA, 2));

        assertThrows(MappedExceptions.PagamentoJaCanceladoException.class, () -> {
            processarPagamento.executar(pagamentoValido, null);
        });
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoParceladoTiverUmaParcela() {
        pagamentoValido.setFormaPagamento(new FormaPagamento(TipoPagamento.PARCELADO_EMISSOR, 1));

        assertThrows(MappedExceptions.PagamentoJaCanceladoException.class, () -> {
            processarPagamento.executar(pagamentoValido, null);
        });
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoDeConflitoQuandoChaveJaExiste() {
        IdempotencyKey chaveSalva = new IdempotencyKey();
        chaveSalva.setResponsePayload("{\"status\":\"AUTORIZADO\"}");

        when(idempotencyRepository.findById(IDEMPOTENCY_KEY)).thenReturn(Optional.of(chaveSalva));

        assertThrows(MappedExceptions.IdempotencyConflictException.class, () -> {
            processarPagamento.executar(pagamentoValido, IDEMPOTENCY_KEY);
        });

        verify(pagamentoRepository, never()).save(any());
    }
}