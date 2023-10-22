package project.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import project.carsharing.dto.car.CarResponseDto;

@Data
public class RentalResponseDto {
    private Long id;
    private CarResponseDto car;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Double paid;
    private Double leftToPay;
}
