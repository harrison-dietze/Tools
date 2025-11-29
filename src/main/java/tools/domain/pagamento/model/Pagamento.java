package tools.domain.pagamento.model;

import tools.domain.pagamento.model.StatusPagamento;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;



@Entity
@Table(name = "pagamentos")
public class Pagamento {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@Pattern(regexp = "^\\d{15}$|^.{0}$", message = "O ID do pagamento deve ser numérico com 15 dígitos ou vazio")
	private String id;

	@NotNull(message = "O campo cartao é obrigatório")
	@Pattern(regexp = "^[0-9\\s]{12,20}$", message = "O número do cartão deve conter apenas dígitos e espaços")
	private String cartao;

	@Embedded
	@Valid
	@NotNull(message = "O objeto descricao é obrigatório")
	private DescricaoPagamento descricao;

	@Embedded
	@Valid
	@NotNull(message = "O objeto formaPagamento é obrigatório")
	private FormaPagamento formaPagamento;

	@Enumerated(EnumType.STRING)
	private StatusPagamento status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StatusPagamento getStatus() {
		return status;
	}

	public void setStatus(StatusPagamento status) {
		this.status = status;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public DescricaoPagamento getDescricao() {
		return descricao;
	}

	public void setDescricao(DescricaoPagamento descricao) {
		this.descricao = descricao;
	}

	public String getCartao() {
		return cartao;
	}

	public void setCartao(String cartao) {
		this.cartao = cartao;
	}
}