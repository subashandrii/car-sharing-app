package project.carsharing.mapper;

import org.mapstruct.Mapper;
import project.carsharing.config.MapperConfig;
import project.carsharing.dto.user.UserRegistrationRequestDto;
import project.carsharing.dto.user.UserResponseDto;
import project.carsharing.dto.user.UserUpdateRequestDto;
import project.carsharing.dto.user.UserUpdateResponseDto;
import project.carsharing.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);
    
    UserUpdateResponseDto toUpdateDto(User user);
    
    User toModel(UserRegistrationRequestDto registrationRequestDto);
    
    User toModel(UserUpdateRequestDto updateRequestDto);
}
