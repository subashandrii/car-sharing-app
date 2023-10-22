package project.carsharing.mapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import project.carsharing.config.MapperConfig;
import project.carsharing.dto.rental.RentalCreateResponseDto;
import project.carsharing.dto.rental.RentalRequestDto;
import project.carsharing.dto.rental.RentalResponseDto;
import project.carsharing.model.Rental;

@Mapper(config = MapperConfig.class, uses = CarMapper.class)
public interface RentalMapper {
    @Mapping(source = "carId", target = "car.id")
    Rental toModel(RentalRequestDto responseDto);
    
    RentalCreateResponseDto toCreateDto(Rental rental);
    
    RentalResponseDto toDto(Rental rental);
    
    @AfterMapping
    default void setTotalAmount(@MappingTarget RentalCreateResponseDto responseDto,
                                Rental rental) {
        long rentalDays = rental.getRentalDate().until(rental.getReturnDate(), ChronoUnit.DAYS) + 1;
        responseDto.setTotalAmount(rentalDays * rental.getCar().getDailyFee().doubleValue());
    }
    
    @AfterMapping
    default void setPaidAndLeftToPay(@MappingTarget RentalResponseDto responseDto,
                                Rental rental) {
        long rentalDays = rental.getRentalDate().until(rental.getReturnDate(), ChronoUnit.DAYS) + 1;
        responseDto.setPaid(rentalDays * rental.getCar().getDailyFee().doubleValue());
        if (rental.getActualReturnDate() != null
                    && rental.getReturnDate().isBefore(LocalDate.now())) {
            long actualRentalDays =
                    rental.getReturnDate().until(rental.getActualReturnDate(), ChronoUnit.DAYS);
            responseDto.setLeftToPay(actualRentalDays
                                             * rental.getCar().getDailyFee().doubleValue());
        }
    }
}
