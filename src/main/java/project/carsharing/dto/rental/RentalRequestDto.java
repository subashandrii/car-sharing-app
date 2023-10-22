package project.carsharing.dto.rental;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalRequestDto {
    @NotNull
    @Min(value = 1)
    private Long carId;
    @NotNull
    @FutureOrPresent
    private LocalDate rentalDate;
    @NotNull
    @FutureOrPresent
    private LocalDate returnDate;
}
