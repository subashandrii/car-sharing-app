package project.carsharing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.carsharing.config.MapperConfig;
import project.carsharing.dto.payment.PaymentResponseDto;
import project.carsharing.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentResponseDto toDto(Payment payment);
}
