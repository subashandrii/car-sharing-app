package project.carsharing.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.user.RoleRequestDto;
import project.carsharing.dto.user.UserResponseDto;
import project.carsharing.mapper.UserMapper;
import project.carsharing.model.User;
import project.carsharing.repository.UserRepository;
import project.carsharing.service.UserService;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    public UserResponseDto getUserByEmail(String email) {
        return userMapper.toDto(userRepository.getUserByEmail(email));
    }
    
    @Override
    @Transactional
    public UserResponseDto updateUsersRole(Long id, RoleRequestDto requestDto, String email) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " is not exist");
        }
        User user = optionalUser.get().setRole(User.Role.valueOf(requestDto.getRole()));
        log.info("Manager with email {} assigned the user with id {} to the role of {}",
                email, id, requestDto.getRole());
        return userMapper.toDto(userRepository.save(user));
    }
}
