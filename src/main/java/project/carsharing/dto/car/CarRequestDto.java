package project.carsharing.dto.car;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarRequestDto {
    @NotNull
    private String model;
    @NotNull
    private String brand;
    @NotNull
    private String type;
    @NotNull
    @Min(value = 1)
    private Integer inventory;
    @NotNull
    @DecimalMin(value = "0.01")
    private Double dailyFee;
}
