package tools.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.domain.pagamento.PagamentoRepository;
import tools.domain.pagamento.model.Pagamento;
import tools.domain.pagamento.model.StatusPagamento;
import tools.exception.MappedExceptions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstornarPagamentoTest {

    @Mock
    private PagamentoRepository repository;

    @InjectMocks
    private EstornarPagamento estornarPagamento;

    private static final String TEST_ID = "123456";
    private Pagamento pagamentoAutorizado;
    private Pagamento pagamentoCancelado;

    @BeforeEach
    void setUp() {
        pagamentoAutorizado = new Pagamento();
        pagamentoAutorizado.setId(TEST_ID);
        pagamentoAutorizado.setStatus(StatusPagamento.AUTORIZADO);

        pagamentoCancelado = new Pagamento();
        pagamentoCancelado.setId(TEST_ID);
        pagamentoCancelado.setStatus(StatusPagamento.CANCELADO);
    }

    @Test
    void deveEstornarPagamentoComSucessoQuandoAutorizado() {
        when(repository.save(any(Pagamento.class))).thenAnswer(i -> i.getArguments()[0]);

        when(repository.findById(TEST_ID)).thenReturn(Optional.of(pagamentoAutorizado));

        Pagamento resultado = estornarPagamento.executar(TEST_ID);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.CANCELADO, resultado.getStatus());
        verify(repository, times(1)).save(pagamentoAutorizado);
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {
        when(repository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(MappedExceptions.PagamentoNaoEncontradoException.class, () -> {
            estornarPagamento.executar(TEST_ID);
        });

        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoJaCancelado() {
        when(repository.findById(TEST_ID)).thenReturn(Optional.of(pagamentoCancelado));

        assertThrows(MappedExceptions.PagamentoJaCanceladoException.class, () -> {
            estornarPagamento.executar(TEST_ID);
        });

        verify(repository, never()).save(any());
    }
}