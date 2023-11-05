package project.carsharing.exception;

public class PaymentException extends CustomRuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
