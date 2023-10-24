package project.carsharing.service;

import project.carsharing.dto.rental.RentalCreateResponseDto;

public interface NotificationService {
    void sendMessageAboutNewRental(RentalCreateResponseDto rentalDto);
    
    void checkOverdueRentalsAndSendMessage();
}
