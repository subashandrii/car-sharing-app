package project.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import project.carsharing.dto.car.CarResponseDto;

@Data
public class RentalCreateResponseDto {
    private Long id;
    private CarResponseDto car;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Double totalAmount;
}
