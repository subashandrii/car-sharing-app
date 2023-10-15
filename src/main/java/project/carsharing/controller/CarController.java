package project.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.carsharing.dto.car.CarRequestDto;
import project.carsharing.dto.car.CarResponseDto;
import project.carsharing.service.CarService;

@Tag(name = "Car management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a car by id")
    public CarResponseDto getCarById(@PathVariable Long id) {
        return carService.findById(id);
    }
    
    @GetMapping
    @Operation(summary = "Get all available cars")
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carService.findAll(pageable);
    }
    
    @PostMapping
    @Operation(summary = "Create a new car")
    public CarResponseDto createCar(@RequestBody @Valid CarRequestDto requestDto) {
        return carService.create(requestDto);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update car information")
    public CarResponseDto updateCar(@PathVariable Long id,
                                    @RequestBody @Valid CarRequestDto requestDto) {
        return carService.update(id, requestDto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a car")
    public void deleteCar(@PathVariable Long id) {
        carService.delete(id);
    }
}
