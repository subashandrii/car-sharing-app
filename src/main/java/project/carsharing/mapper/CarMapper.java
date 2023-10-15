package project.carsharing.mapper;

import org.mapstruct.Mapper;
import project.carsharing.config.MapperConfig;
import project.carsharing.dto.car.CarRequestDto;
import project.carsharing.dto.car.CarResponseDto;
import project.carsharing.model.Car;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarResponseDto toDto(Car car);
    
    Car toModel(CarRequestDto requestDto);
}
