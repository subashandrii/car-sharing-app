package project.carsharing.service;

import project.carsharing.model.Payment;

public interface NotificationService {
    void sendMessageAboutNewRental(Long rentalId, Payment payment);
}
