package project.carsharing.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.rental.RentalCreateResponseDto;
import project.carsharing.dto.rental.RentalRequestDto;
import project.carsharing.dto.rental.RentalResponseDto;
import project.carsharing.exception.RentalException;
import project.carsharing.mapper.RentalMapper;
import project.carsharing.model.Car;
import project.carsharing.model.Payment;
import project.carsharing.model.Rental;
import project.carsharing.model.User;
import project.carsharing.repository.CarRepository;
import project.carsharing.repository.RentalRepository;
import project.carsharing.repository.UserRepository;
import project.carsharing.service.NotificationService;
import project.carsharing.service.PaymentService;
import project.carsharing.service.RentalService;

@Log4j2
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    
    @Override
    public RentalResponseDto findById(String email, Long id) {
        Rental rental = getRentalById(id);
        if (!email.equals(rental.getUser().getEmail())) {
            throw new RuntimeException("You don`t have rental with this " + id);
        }
        return rentalMapper.toDto(rental);
    }
    
    @Override
    public List<RentalResponseDto> getAllByUser(String email, Pageable pageable) {
        return rentalRepository.findAllByUserEmail(email, pageable).stream()
                       .map(rentalMapper::toDto)
                       .toList();
    }
    
    @Override
    public List<RentalResponseDto> findAllIfTheyAreActive(Long userId,
                                                          boolean isActive,
                                                          Pageable pageable) {
        List<Rental> rentals = getRentals(userId, isActive, pageable);
        return rentals.stream()
                       .map(rentalMapper::toDto)
                       .toList();
    }
    
    @Override
    @Transactional
    public RentalCreateResponseDto create(RentalRequestDto requestDto,
                                          String email) {
        Car car = carRepository.getReferenceById(requestDto.getCarId());
        User user = userRepository.getUserByEmail(email);
        checkRentalBeforeCreating(car, user);
        Car savedCar = carRepository.save(car.setInventory(car.getInventory() - 1));
        
        Rental rental = rentalMapper.toModel(requestDto)
                                .setCar(savedCar)
                                .setUser(user)
                                .setRentalDate(LocalDate.now())
                                .setStatus(Rental.Status.CREATED);
        Rental savedRental = rentalRepository.save(rental);
        log.info("User with email {} created a new rental {}", email, savedRental);
        return rentalMapper.toCreateDto(savedRental);
    }
    
    @Override
    @Transactional
    public void setPaidStatus(Long rentalId, String email, Payment payment) {
        Rental rental = getRentalById(rentalId);
        rental.setStatus(Rental.Status.PAID);
        Rental savedRental = rentalRepository.save(rental);
        log.info("User with email {} paid a new rental {}", email, savedRental);
        notificationService.sendMessageAboutNewRental(rentalId, payment);
    }
    
    @Override
    @Transactional
    public RentalResponseDto returnCarAfterRental(Long id, String email) {
        Rental rental = getRentalById(id);
        checkRentalBeforeReturningCar(rental, email);
        
        Car car = rental.getCar();
        rental.setCar(car.setInventory(car.getInventory() + 1))
                .setActualReturnDate(LocalDate.now())
                .setStatus(Rental.Status.RETURNED);
        log.info("Manager with email {} processed the return "
                         + "of the car after the rental {}", email, rental.toString());
        return rentalMapper.toDto(rentalRepository.save(rental));
    }
    
    @Override
    @Transactional
    public RentalResponseDto setCancelledStatusForCreatedRental(Long id, String email) {
        Rental rental = getRentalById(id);
        checkRentalBeforeCanceling(rental, email);
        
        Rental savedRental = rentalRepository.save(setCancelledStatus(rental));
        log.info("User with email {} canceled the rental with ID {}", email, rental.getId());
        return rentalMapper.toDto(savedRental);
    }
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void setCancelledStatusForUnpaidRentals() {
        List<Rental> rentals = rentalRepository.getRentalsByStatus(Rental.Status.CREATED).stream()
                                       .map(this::setCancelledStatus)
                                       .toList();
        rentalRepository.saveAll(rentals);
        log.info("The status CREATED of " + rentals.size()
                         + " rentals has been changed to CANCELLED");
    }
    
    private Rental setCancelledStatus(Rental rental) {
        Car car = rental.getCar();
        Car savedCar = carRepository.save(car.setInventory(car.getInventory() + 1));
        return rental.setCar(savedCar)
                .setStatus(Rental.Status.CANCELLED);
    }
    
    private void checkRentalBeforeCreating(Car car, User user) {
        List<Rental> rentals = rentalRepository.getRentalsByUserAndStatusIn(
                user, List.of(Rental.Status.PAID, Rental.Status.CREATED)
        );
        if (!rentals.isEmpty()) {
            log.error("User with email {} was unable to rent a car (ID {}) because he "
                              + "has an outstanding rental", user.getEmail(), car.getId());
            throw new RentalException("You cannot rental a car because "
                                               + "you have an outstanding lease");
        } else if (car.getInventory() <= 0) {
            log.error("User with email {} could not rent a car (ID {}) "
                              + "because it is not available", user.getEmail(), car.getId());
            throw new RentalException("There are no available cars, please choose another car");
        } else if (LocalTime.now().getHour() == 23 && LocalTime.now().getMinute() >= 50) {
            log.error("User with email {} could not rent a car (ID {}) "
                              + "after 23:50 ({})", user.getEmail(), car.getId(), LocalTime.now());
            throw new RentalException("You cannot rent a car after 23:50, "
                                              + "please try after midnight");
        }
    }
    
    private void checkRentalBeforeCanceling(Rental rental, String email) {
        if (!rental.getStatus().equals(Rental.Status.CREATED)) {
            log.error("User with email {} could not cancel the rental "
                              + "because it has the status of {}", email, rental.getStatus());
            throw new RentalException("You can not cancel this rental");
        } else if (!rental.getUser().getEmail().equals(email)) {
            log.error("User with email {} could not cancel the rental"
                              + " because it does not belong to him", email);
            throw new RentalException("You can not cancel this rental");
        }
    }
    
    private void checkRentalBeforeReturningCar(Rental rental, String email) {
        if (!rental.getStatus().equals(Rental.Status.PAID)) {
            log.error("Manager with an email {} attempted to process a rental"
                              + " car return (rental ID {}) but rental status is {}",
                    email, rental.getId(), rental.getStatus());
            throw new RentalException("It is not possible to return the car because the "
                                               + "rental status is " + rental.getStatus());
        } else if (!paymentService.isRentalPaid(rental.getId(), Payment.Type.FINE)
                            && rental.getReturnDate().isBefore(LocalDate.now())) {
            log.error("Manager with an email {} attempted to process a rental"
                              + " car return (rental ID {}) but the fine was not paid",
                    email, rental.getId());
            throw new RentalException("The late rental (ID " + rental.getId()
                                               + ") fine has not been paid");
        }
    }
    
    private Rental getRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(
                () -> new EntityNotFoundException("Rental with id " + rentalId + " is not exist"));
    }
    
    private List<Rental> getRentals(Long userId, boolean isActive, Pageable pageable) {
        if (userId != null) {
            return isActive ? rentalRepository
                                      .findAllByUserIdAndActualReturnDateIsNull(userId, pageable)
                           : rentalRepository
                                     .findAllByUserIdAndActualReturnDateIsNotNull(userId, pageable);
        } else {
            return isActive ? rentalRepository
                                      .findAllByActualReturnDateIsNull(pageable)
                            : rentalRepository
                                      .findAllByActualReturnDateIsNotNull(pageable);
        }
    }
}
