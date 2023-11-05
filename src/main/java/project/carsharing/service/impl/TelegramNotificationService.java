package project.carsharing.service.impl;

import static project.carsharing.util.PatternUtil.DATE_FORMAT;
import static project.carsharing.util.PatternUtil.DATE_TIME_FORMAT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.rental.RentalResponseDto;
import project.carsharing.mapper.RentalMapper;
import project.carsharing.model.Payment;
import project.carsharing.model.Rental;
import project.carsharing.repository.RentalRepository;
import project.carsharing.repository.UserRepository;
import project.carsharing.service.NotificationService;
import project.carsharing.service.api.TelegramBotApi;

@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramNotificationService implements NotificationService {
    private final TelegramBotApi telegramBotApi;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    
    @Override
    @Transactional
    public void sendMessageAboutNewRental(Long rentalId, Payment payment) {
        Rental rental = rentalRepository.getReferenceById(rentalId);
        String message = createMessageAboutNewRental(rental, payment);
        sendMessageToAllManagers(message);
        log.info("All managers have been notified by telegram bot"
                          + " about creation of a new rental with id {}", rental.getId());
    }
    
    @Scheduled(cron = "0 0 11 * * ?")
    @Transactional
    public void checkOverdueRentalsAndSendMessage() {
        List<RentalResponseDto> rentals =
                rentalRepository.findAllByReturnDateBefore(LocalDate.now()).stream()
                        .map(rentalMapper::toDto)
                        .toList();
        String message = rentals.isEmpty()
                              ? "Hello! No rentals overdue today!"
                              : createMessageAboutOverdueRentals(rentals);
        sendMessageToAllManagers(message);
        log.info("All managers have been notified by telegram bot"
                          + " about {} overdue rentals", rentals.size());
    }
    
    private String createMessageAboutNewRental(Rental rental, Payment payment) {
        
        String message = """
                🆕 Here is a new rental that was created on %s by user %s %s (%s)
                
                🚙 %s %s
                ➡ %s - rental date
                ⬅ %s - return date
                💳 %s USD - paid""";
        return String.format(message,
                LocalDateTime.now().format(DATE_TIME_FORMAT),
                rental.getUser().getFirstName(),
                rental.getUser().getLastName(),
                rental.getUser().getEmail(),
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getRentalDate().format(DATE_FORMAT),
                rental.getReturnDate().format(DATE_FORMAT),
                payment.getAmount().doubleValue());
    }
    
    private String createMessageAboutOverdueRentals(List<RentalResponseDto> rentalsBeforeDate) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\uD83D\uDC4B Hello, today there are ")
                .append(rentalsBeforeDate.size())
                .append(" overdue car rentals!\n");
        
        for (int i = 0; i < rentalsBeforeDate.size(); i++) {
            long overdueDays = rentalsBeforeDate.get(i).getReturnDate()
                                       .until(LocalDate.now(), ChronoUnit.DAYS);
            RentalResponseDto rental = rentalsBeforeDate.get(i);
            String rentMessage = """
                    
                    %s. %s - return date (%s %s overdue)
                        %s %s
                        %s %s (%s)
                    """;
            stringBuilder.append(String.format(rentMessage,
                    i + 1,
                    rental.getReturnDate().format(DATE_FORMAT),
                    overdueDays,
                    overdueDays == 1 ? "day" : "days",
                    rental.getCar().getBrand(),
                    rental.getCar().getModel(),
                    rental.getUser().getFirstName(),
                    rental.getUser().getLastName(),
                    rental.getUser().getEmail()));
        }
        return stringBuilder.toString();
    }
    
    private void sendMessageToAllManagers(String message) {
        userRepository.findAllByTelegramChatIdIsNotNull()
                .forEach(user -> telegramBotApi.sendMessage(user.getTelegramChatId(), message));
    }
}
