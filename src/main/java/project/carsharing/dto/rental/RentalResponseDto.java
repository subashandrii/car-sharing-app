package project.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;
import project.carsharing.dto.car.CarResponseDto;
import project.carsharing.dto.user.UserResponseDto;

@Data
@Accessors(chain = true)
public class RentalResponseDto {
    private Long id;
    private UserResponseDto user;
    private CarResponseDto car;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Double paid;
    private Double leftToPay;
}
