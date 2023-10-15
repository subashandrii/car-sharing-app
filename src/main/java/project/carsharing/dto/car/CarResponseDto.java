package project.carsharing.dto.car;

import lombok.Data;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private String type;
    private Integer inventory;
    private Double dailyFee;
}
