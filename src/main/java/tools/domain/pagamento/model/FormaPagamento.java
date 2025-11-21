package tools.domain.pagamento.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class FormaPagamento {
    private TipoPagamento tipo; 
    
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
    
    
}