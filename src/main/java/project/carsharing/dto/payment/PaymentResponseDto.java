package project.carsharing.dto.payment;

import lombok.Data;
import lombok.experimental.Accessors;
import project.carsharing.model.Payment;

@Data
@Accessors(chain = true)
public class PaymentResponseDto {
    private Long id;
    private Long rentalId;
    private String sessionId;
    private Payment.Status status;
    private Payment.Type type;
    private Double amount;
    private String message;
}
