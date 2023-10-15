package project.carsharing.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.carsharing.dto.car.CarRequestDto;
import project.carsharing.dto.car.CarResponseDto;

public interface CarService {
    CarResponseDto findById(Long id);
    
    List<CarResponseDto> findAll(Pageable pageable);
    
    CarResponseDto create(CarRequestDto requestDto);
    
    CarResponseDto update(Long id, CarRequestDto requestDto);
    
    void delete(Long id);
}
