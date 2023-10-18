package project.carsharing.service;

import project.carsharing.dto.user.RoleRequestDto;
import project.carsharing.dto.user.UserResponseDto;

public interface UserService {
    UserResponseDto getUserByEmail(String email);
    
    UserResponseDto updateUsersRole(Long id, RoleRequestDto requestDto);
}
