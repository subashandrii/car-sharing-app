package project.carsharing.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.car.CarRequestDto;
import project.carsharing.dto.car.CarResponseDto;
import project.carsharing.mapper.CarMapper;
import project.carsharing.model.Car;
import project.carsharing.repository.CarRepository;
import project.carsharing.service.CarService;

@Log4j2
@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    
    @Override
    public CarResponseDto findById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Car with id " + id + " is not exist"));
        return carMapper.toDto(car);
    }
    
    @Override
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                       .map(carMapper::toDto)
                       .toList();
    }
    
    @Override
    @Transactional
    public CarResponseDto create(CarRequestDto requestDto, String email) {
        Car savedCar = carRepository.save(carMapper.toModel(requestDto));
        log.info("Manager with email {} created car {}",
                email, savedCar.toString());
        return carMapper.toDto(savedCar);
    }
    
    @Override
    @Transactional
    public CarResponseDto update(Long id, CarRequestDto requestDto, String email) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Car with id " + id + " is not exist");
        }
        Car car = carMapper.toModel(requestDto).setId(id);
        log.info("Manager with email {} updated car {}",
                email, car.toString());
        return carMapper.toDto(carRepository.save(car));
    }
    
    @Override
    @Transactional
    public void delete(Long id, String email) {
        carRepository.deleteById(id);
        log.info("Manager with email {} deleted car wit id {}",
                email, id);
    }
}
