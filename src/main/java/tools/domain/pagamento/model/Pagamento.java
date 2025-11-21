package tools.domain.pagamento.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id; 

    private String cartao; 

    @Embedded
    private DescricaoPagamento descricao; 

    @Embedded
    private FormaPagamento formaPagamento; 
    
    @Enumerated(EnumType.STRING)
    private StatusPagamento status; 

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCartao() {
		return cartao;
	}
	public void setCartao(String cartao) {
		this.cartao = cartao;
	}
	public DescricaoPagamento getDescricao() {
		return descricao;
	}
	public void setDescricao(DescricaoPagamento descricao) {
		this.descricao = descricao;
	}
	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}
	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	public StatusPagamento getStatus() {
		return status;
	}
	public void setStatus(StatusPagamento status) {
		this.status = status;
	}
    
    
}