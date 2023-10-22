package project.carsharing.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.rental.RentalCreateResponseDto;
import project.carsharing.dto.rental.RentalRequestDto;
import project.carsharing.dto.rental.RentalResponseDto;
import project.carsharing.mapper.RentalMapper;
import project.carsharing.model.Car;
import project.carsharing.model.Rental;
import project.carsharing.repository.CarRepository;
import project.carsharing.repository.RentalRepository;
import project.carsharing.repository.UserRepository;
import project.carsharing.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    
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
    public RentalCreateResponseDto create(String email, RentalRequestDto requestDto) {
        Car car = carRepository.getReferenceById(requestDto.getCarId());
        if (car.getInventory() <= 0) {
            throw new RuntimeException("There are no available cars, please choose another car");
        }
        car.setInventory(car.getInventory() - 1);
        Car savedCar = carRepository.save(car);
        Rental rental = rentalMapper.toModel(requestDto)
                                .setCar(savedCar)
                                .setUser(userRepository.getUserByEmail(email));
        return rentalMapper.toCreateDto(rentalRepository.save(rental));
    }
    
    @Override
    @Transactional
    public RentalResponseDto returnCarAfterRental(Long id) {
        Rental rental = getRentalById(id);
        if (rental.getActualReturnDate() != null) {
            throw new RuntimeException("This car is returned");
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        rental.setCar(car)
                .setActualReturnDate(LocalDate.now());
        return rentalMapper.toDto(rentalRepository.save(rental));
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
