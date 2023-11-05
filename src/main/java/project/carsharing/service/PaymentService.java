package project.carsharing.service;

import com.stripe.model.checkout.Session;
import java.util.List;
import org.springframework.data.domain.Pageable;
import project.carsharing.dto.payment.PaymentRequestDto;
import project.carsharing.dto.payment.PaymentResponseDto;
import project.carsharing.model.Payment;

public interface PaymentService {
    
    Payment createPayment(PaymentRequestDto requestDto, String email);
    
    void updatePayment(Session session, Payment payment);
    
    PaymentResponseDto setStatus(Payment payment, Payment.Status status);
    
    boolean isRentalPaid(Long rentalId, Payment.Type paymentType);
    
    boolean isSessionPaid(String sessionId);
    
    Payment getPaymentBySessionId(String sessionId);
    
    List<PaymentResponseDto> getPaymentsByUserEmail(Pageable pageable, String email);
    
    List<PaymentResponseDto> getPaymentsByUserId(Pageable pageable, Long userId);
}
