package tools.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import tools.usecase.ProcessarPagamento;
import tools.usecase.EstornarPagamento;
import tools.usecase.DetalharPagamento;
import tools.usecase.ListarPagamentos;
import tools.domain.pagamento.model.Pagamento;
import tools.exception.MappedExceptions;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagamentoResource.class)
public class PagamentoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessarPagamento processarPagamento;
    @MockBean
    private EstornarPagamento estornarPagamento;
    @MockBean
    private DetalharPagamento detalharPagamento;
    @MockBean
    private ListarPagamentos listarPagamentos;

    private static final String TEST_ID = "100023568900001";
    private static final String BASE_URL = "/api/pagamentos";

    private Pagamento criarPagamentoSimulado(String status) {
        Pagamento p = new Pagamento();
        p.setId(TEST_ID);
        return p;
    }

    @Test
    void deveProcessarPagamentoComSucessoERetornar201() throws Exception {
        Pagamento pagamentoAutorizado = criarPagamentoSimulado("AUTORIZADO");

        when(processarPagamento.executar(any(Pagamento.class), any(String.class)))
                .thenReturn(pagamentoAutorizado);

        String payload = objectMapper.writeValueAsString(Collections.singletonMap("pagamento", pagamentoAutorizado));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", "teste-idempotencia-1")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pagamento.id").value(TEST_ID));
    }

    @Test
    void deveRetornarRespostaSalvaEmConflitoDeIdempotencia() throws Exception {
        String respostaSalva = "{\"pagamento\":{\"id\":\"" + TEST_ID + "\", \"status\":\"AUTORIZADO\"}}";

        when(processarPagamento.executar(any(Pagamento.class), eq("chave-duplicada")))
                .thenThrow(new MappedExceptions.IdempotencyConflictException("Conflito", respostaSalva));

        Pagamento pagamento = criarPagamentoSimulado("DUMMY");
        String payload = objectMapper.writeValueAsString(Collections.singletonMap("pagamento", pagamento));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", "chave-duplicada")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pagamento.id").value(TEST_ID));
    }

    @Test
    void deveEstornarPagamentoComSucessoERetornar200() throws Exception {
        Pagamento pagamentoCancelado = criarPagamentoSimulado("CANCELADO");

        when(estornarPagamento.executar(eq(TEST_ID)))
                .thenReturn(pagamentoCancelado);

        mockMvc.perform(post(BASE_URL + "/{id}/estorno", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagamento.id").value(TEST_ID));
    }

    @Test
    void deveDetalharPagamentoComSucessoERetornar200() throws Exception {
        Pagamento pagamento = criarPagamentoSimulado("AUTORIZADO");

        when(detalharPagamento.executar(eq(TEST_ID)))
                .thenReturn(Optional.of(pagamento));

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagamento.id").value(TEST_ID));
    }

    @Test
    void deveRetornar404QuandoPagamentoNaoEncontrado() throws Exception {

        when(detalharPagamento.executar(any(String.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/{id}", "ID_INEXISTENTE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar400QuandoEstornoJaCancelado() throws Exception {

        when(estornarPagamento.executar(any(String.class)))
                .thenThrow(new MappedExceptions.PagamentoJaCanceladoException("Pagamento já cancelado."));

        mockMvc.perform(post(BASE_URL + "/{id}/estorno", TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}