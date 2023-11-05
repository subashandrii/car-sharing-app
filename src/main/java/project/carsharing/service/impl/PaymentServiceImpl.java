package project.carsharing.service.impl;

import static project.carsharing.model.Payment.Type.valueOf;
import static project.carsharing.util.PatternUtil.formatDoubleValue;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.payment.PaymentRequestDto;
import project.carsharing.dto.payment.PaymentResponseDto;
import project.carsharing.exception.PaymentException;
import project.carsharing.mapper.PaymentMapper;
import project.carsharing.model.Payment;
import project.carsharing.model.Rental;
import project.carsharing.repository.PaymentRepository;
import project.carsharing.repository.RentalRepository;
import project.carsharing.service.PaymentService;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    private static final Double FINE_MULTIPLIER = 1.15;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    
    @Override
    @Transactional
    public Payment createPayment(PaymentRequestDto requestDto, String email) {
        Rental rental = rentalRepository.getReferenceById(requestDto.getRentalId());
        BigDecimal amount = BigDecimal.valueOf(
                formatDoubleValue(getAmount(valueOf(requestDto.getPaymentType()), rental)));
        checkRentalBeforeCreating(rental, valueOf(requestDto.getPaymentType()), email);
        
        Payment payment = new Payment()
                                  .setAmount(amount)
                                  .setStatus(Payment.Status.PENDING)
                                  .setType(valueOf(requestDto.getPaymentType()))
                                  .setRental(rental);
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public void updatePayment(Session session, Payment payment) {
        payment.setSessionUrl(session.getUrl())
                .setSessionId(session.getId());
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment was created " + savedPayment);
    }
    
    @Override
    @Transactional
    public PaymentResponseDto setStatus(Payment payment, Payment.Status status) {
        payment.setStatus(status);
        Payment savedPayment = paymentRepository.save(payment);
        log.info(status.equals(Payment.Status.PAID)
                         ? "The payment was successful ID " + payment.getId()
                         : "There was a payment error ID " + payment.getId()
                                   + ". You can pay by the end of the day using this link");
        return paymentMapper.toDto(savedPayment).setMessage(getPaymentMessage(status));
    }
    
    @Override
    @Transactional
    public boolean isRentalPaid(Long rentalId, Payment.Type paymentType) {
        List<Payment> payments = paymentRepository.getPaymentsByRentalId(rentalId);
        return payments.stream().anyMatch(payment -> payment.getStatus().equals(Payment.Status.PAID)
                                                    && payment.getType().equals(paymentType));
    }
    
    @Override
    public boolean isSessionPaid(String sessionId) {
        try {
            return Session.retrieve(sessionId).getPaymentStatus().equals("paid");
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.getPaymentBySessionId(sessionId);
    }
    
    @Override
    @Transactional
    public List<PaymentResponseDto> getPaymentsByUserEmail(Pageable pageable, String email) {
        return paymentRepository.getPaymentsByRentalUserEmail(email, pageable).stream()
                       .map(paymentMapper::toDto)
                       .toList();
    }
    
    @Override
    @Transactional
    public List<PaymentResponseDto> getPaymentsByUserId(Pageable pageable, Long userId) {
        return paymentRepository.getPaymentsByRentalUserId(userId, pageable).stream()
                       .map(paymentMapper::toDto)
                       .toList();
    }
    
    private double getAmount(Payment.Type paymentType, Rental rental) {
        return paymentType.equals(Payment.Type.FINE)
                       ? rental.getCar().getDailyFee().doubleValue()
                                 * rental.getReturnDate().until(LocalDate.now(), ChronoUnit.DAYS)
                                 * FINE_MULTIPLIER
                       : (rental.getRentalDate().until(rental.getReturnDate(), ChronoUnit.DAYS) + 1)
                                  * rental.getCar().getDailyFee().doubleValue();
    }
    
    private String getPaymentMessage(Payment.Status status) {
        return status.equals(Payment.Status.PAID)
                       ? "The payment was successful"
                       : "Something went wrong!";
    }
    
    private void checkRentalBeforeCreating(Rental rental, Payment.Type type, String email) {
        if (isRentalPaid(rental.getId(), type)) {
            log.error("User with email {} tried to pay ({}) the rental paid {}",
                    email, type, rental);
            throw new PaymentException("This rent has already been paid");
        } else if (rental.getStatus().equals(Rental.Status.CANCELLED)
                           || rental.getStatus().equals(Rental.Status.RETURNED)) {
            log.error("User with email {} failed to pay because the rental "
                              + "(ID {}) status is {}", email, rental.getId(), rental.getStatus());
            throw new PaymentException("It is not possible to pay for the rental, "
                                               + "please create a new one");
        } else if (type.equals(Payment.Type.FINE)
                            && rental.getReturnDate().isAfter(LocalDate.now())) {
            log.warn("The user does not need to pay a rental fine, "
                             + "because it is not overdue ({})", rental);
            throw new PaymentException("There is no need to pay a fine");
        }
    }
}
