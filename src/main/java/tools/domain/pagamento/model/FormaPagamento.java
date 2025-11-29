package tools.domain.pagamento.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class FormaPagamento {

    @NotNull(message = "O tipo de pagamento é obrigatório")
	@Enumerated(EnumType.STRING)
	private TipoPagamento tipo;

	@NotNull(message = "O número de parcelas é obrigatório")
	@Min(value = 1, message = "O pagamento deve ter no mínimo 1 parcela")
	@Max(value = 99, message = "O número máximo de parcelas é 99")
	private Integer parcelas;

	public TipoPagamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoPagamento tipo) {
		this.tipo = tipo;
	}

	public Integer getParcelas() {
		return parcelas;
	}

	public void setParcelas(Integer parcelas) {
		this.parcelas = parcelas;
	}

	public FormaPagamento(TipoPagamento tipo, Integer parcelas) {
		this.parcelas = parcelas;
		this.tipo = tipo;
	}
}