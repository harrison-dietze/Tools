package tools.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentResource {

	@Autowired
	private PaymentService paymentService;

	@GetMapping
	public List<Payment> getAllPayments() {
		return paymentService.getAll();
	}

	@GetMapping("/{id}")
	public Payment getPaymentById(@PathVariable Long id) {
		return paymentService.getById(id);
	}

	@PostMapping
	public Payment pay(@RequestBody PaymentRequest paymentRequest) {
		return paymentService.pay(paymentRequest);
	}
}
