package project.carsharing.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.carsharing.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment getPaymentBySessionId(String sessionId);
    
    List<Payment> getPaymentsByRentalId(Long rentalId);
    
    List<Payment> getPaymentsByRentalUserId(Long id, Pageable pageable);
    
    List<Payment> getPaymentsByRentalUserEmail(String email, Pageable pageable);
}
