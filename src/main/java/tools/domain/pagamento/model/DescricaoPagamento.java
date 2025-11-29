package tools.domain.pagamento.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;


@Embeddable
public class DescricaoPagamento {

	@NotNull(message = "O valor é obrigatório")
	@Pattern(regexp = "^\\d+(\\.\\d{2})?$", message = "O valor deve ser um número com até 2 casas decimais (ex: 500.50)")
	private String valor;

	@NotNull(message = "A data e hora são obrigatórias")
	@Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$", message = "O formato da data e hora deve ser dd/MM/yyyy HH:mm:ss")
	private String dataHora;

	@NotBlank(message = "O estabelecimento é obrigatório")
	private String estabelecimento;

	private String nsu;
	private String codigoAutorizacao;

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getDataHora() {
		return dataHora;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public String getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(String estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public String getNsu() {
		return nsu;
	}

	public void setNsu(String nsu) {
		this.nsu = nsu;
	}

	public String getCodigoAutorizacao() {
		return codigoAutorizacao;
	}

	public void setCodigoAutorizacao(String codigoAutorizacao) {
		this.codigoAutorizacao = codigoAutorizacao;
	}
}