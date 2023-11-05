package project.carsharing.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import project.carsharing.model.Payment;
import project.carsharing.validation.annotation.EnumValueCheck;

@Data
public class PaymentRequestDto {
    @NotNull
    @Min(1)
    private Long rentalId;
    @EnumValueCheck(Payment.Type.class)
    private String paymentType;
}
