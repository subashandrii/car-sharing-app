package project.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import project.carsharing.dto.car.CarResponseDto;
import project.carsharing.dto.user.UserResponseDto;
import project.carsharing.model.Rental;

@Data
public class RentalCreateResponseDto {
    private Long id;
    private UserResponseDto user;
    private CarResponseDto car;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Rental.Status status;
}
