package project.carsharing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.carsharing.config.MapperConfig;
import project.carsharing.dto.rental.RentalCreateResponseDto;
import project.carsharing.dto.rental.RentalRequestDto;
import project.carsharing.dto.rental.RentalResponseDto;
import project.carsharing.model.Rental;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class})
public interface RentalMapper {
    @Mapping(source = "carId", target = "car.id")
    Rental toModel(RentalRequestDto responseDto);
    
    RentalCreateResponseDto toCreateDto(Rental rental);
    
    RentalResponseDto toDto(Rental rental);
}
