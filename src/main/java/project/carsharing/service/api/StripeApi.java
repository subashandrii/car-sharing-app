package project.carsharing.service.api;

import static project.carsharing.model.Payment.Type.valueOf;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.carsharing.dto.payment.PaymentRequestDto;
import project.carsharing.model.Payment;
import project.carsharing.model.Rental;
import project.carsharing.repository.RentalRepository;

@Component
@RequiredArgsConstructor
public class StripeApi {
    private static final String CURRENCY = "usd";
    private static final String SUCCESS_URL = "http://localhost:8082/api/payments/success";
    private static final String CANCEL_URL = "http://localhost:8082/api/payments/cancel";
    private final RentalRepository rentalRepository;
    @Value("${stripe.api.key}")
    private String secretKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
    
    public Session createSession(PaymentRequestDto requestDto, BigDecimal amount)
            throws StripeException {
        Rental rental = rentalRepository.getReferenceById(requestDto.getRentalId());
        Map<String, String> paymentNameAndDescription =
                createPaymentNameAndDescription(
                        valueOf(requestDto.getPaymentType()), rental);
        SessionCreateParams params = SessionCreateParams.builder()
                                  .setCustomerEmail(rental.getUser().getEmail())
                                  .setCancelUrl(CANCEL_URL + "?session_id={CHECKOUT_SESSION_ID}")
                                  .setSuccessUrl(SUCCESS_URL + "?session_id={CHECKOUT_SESSION_ID}")
                                  .setMode(SessionCreateParams.Mode.PAYMENT)
                                  .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                  .addLineItem(SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setUnitAmountDecimal(amount.multiply(BigDecimal.valueOf(100)))
                        .setCurrency(CURRENCY)
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(paymentNameAndDescription.get("name"))
                                .setDescription(paymentNameAndDescription.get("description"))
                                .build())
                        .build())
                .build())
                                  .build();
        return Session.create(params);
    }
    
    private Map<String, String> createPaymentNameAndDescription(Payment.Type paymentType,
                                                                Rental rental) {
        String name = paymentType.equals(Payment.Type.FINE)
                              ? "Payment of overdue days for renting a car. Rental ID "
                                        + rental.getId()
                              : "Payment for car rental. Rental ID " + rental.getId();
        
        String description = paymentType.equals(Payment.Type.FINE)
                                      ? String.format("Payment of %s day(s) overdue for "
                                                              + "%s %s car rental",
                                                rental.getReturnDate().until(
                                                        LocalDate.now(),ChronoUnit.DAYS),
                                                rental.getCar().getBrand(),
                                                rental.getCar().getModel())
                                      : String.format("Rental %s %s car for %s %s",
                                                rental.getCar().getBrand(),
                                                rental.getCar().getModel(),
                                                rental.getRentalDate().until(
                                                        rental.getReturnDate(),ChronoUnit.DAYS) + 1,
                                                rental.getReturnDate()
                                                        .equals(LocalDate.now()) ? "day" : "days");
        return Map.of("name", name, "description", description);
    }
}
