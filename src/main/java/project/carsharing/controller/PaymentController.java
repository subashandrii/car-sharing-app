package project.carsharing.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.carsharing.dto.payment.PaymentRequestDto;
import project.carsharing.dto.payment.PaymentResponseDto;
import project.carsharing.exception.PaymentException;
import project.carsharing.model.Payment;
import project.carsharing.service.PaymentService;
import project.carsharing.service.RentalService;
import project.carsharing.service.api.StripeApi;

@Tag(name = "Payment management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final StripeApi stripeApi;
    
    @GetMapping
    @Operation(summary = "Get my payments")
    public List<PaymentResponseDto> getPayments(Pageable pageable, Authentication authentication) {
        return paymentService.getPaymentsByUserEmail(pageable, authentication.getName());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Get payments by user id")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<PaymentResponseDto> getPaymentsByUserId(@RequestParam(name = "user_id") Long userId,
                                                        Pageable pageable) {
        return paymentService.getPaymentsByUserId(pageable, userId);
    }
    
    @PostMapping
    @Operation(summary = "Create a new payment")
    public String createPayment(@RequestBody @Valid PaymentRequestDto requestDto,
                                Authentication authentication)
            throws StripeException {
        Payment payment = paymentService.createPayment(requestDto, authentication.getName());
        Session session = stripeApi.createSession(requestDto, payment.getAmount());
        paymentService.updatePayment(session, payment);
        return "redirect:" + session.getUrl();
    }
    
    @GetMapping("/success")
    @Operation(summary = "Get payment dto after successful payment")
    public PaymentResponseDto successPayment(@RequestParam(name = "session_id") String sessionId,
                                             Authentication authentication) {
        if (paymentService.isSessionPaid(sessionId)) {
            Payment payment = paymentService.getPaymentBySessionId(sessionId);
            if (payment.getType().equals(Payment.Type.PAYMENT)) {
                rentalService.setPaidStatus(payment.getRental().getId(),
                        authentication.getName(), payment);
            }
            return paymentService.setStatus(payment, Payment.Status.PAID);
        } else {
            throw new PaymentException("Session " + sessionId + " is not paid");
        }
    }
    
    @GetMapping("/cancel")
    @Operation(summary = "Get payment dto after unsuccessful payment")
    public PaymentResponseDto cancelPayment(@RequestParam(name = "session_id") String sessionId) {
        Payment payment = paymentService.getPaymentBySessionId(sessionId);
        return paymentService.setStatus(payment, Payment.Status.PAYMENT_ERROR);
    }
}
