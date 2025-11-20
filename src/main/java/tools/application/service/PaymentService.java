package tools.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tools.domain.model.Payment;
import tools.domain.repository.PaymentRepository;

import java.util.List;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;

	public List<Payment> getAll() {
		return paymentRepository.findAll();
	}

	public Noticia getById(Long id) {
		return paymentRepository.findById(id).orElse(null);
	}

	public Payment pay(PaymentRequest paymentRequest) {
		return paymentRepository.save(noticia);
	}
}
