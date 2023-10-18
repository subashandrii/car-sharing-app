package project.carsharing.secure;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.carsharing.dto.user.UserLoginRequestDto;
import project.carsharing.dto.user.UserLoginResponseDto;
import project.carsharing.dto.user.UserRegistrationRequestDto;
import project.carsharing.dto.user.UserResponseDto;
import project.carsharing.dto.user.UserUpdateRequestDto;
import project.carsharing.dto.user.UserUpdateResponseDto;
import project.carsharing.exception.RegistrationException;
import project.carsharing.mapper.UserMapper;
import project.carsharing.model.User;
import project.carsharing.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto,
                                    boolean isItManager)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration!");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        if (isItManager) {
            user.setRole(User.Role.MANAGER);
        }
        return userMapper.toDto(userRepository.save(user));
    }
    
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
                        requestDto.getPassword()));
        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }
    
    @Transactional
    public UserUpdateResponseDto updateProfileInfo(String email,
                                                   UserUpdateRequestDto requestDto) {
        User userFromDb = userRepository.getUserByEmail(email);
        Optional<User> userByRequestEmail = userRepository.findByEmail(requestDto.getEmail());
        if (userByRequestEmail.isPresent() && !requestDto.getEmail().equals(email)) {
            throw new RuntimeException("User with this email is exist");
        }
        User savedUser = userRepository.save(updateUser(userFromDb, requestDto));
        String token = null;
        if (!savedUser.getEmail().equals(email)) {
            token = updateAuthentication(savedUser);
        }
        return userMapper.toUpdateDto(savedUser).setToken(token);
    }
    
    private User updateUser(User user, UserUpdateRequestDto requestDto) {
        User updatedUser = userMapper.toModel(requestDto)
                                   .setId(user.getId())
                                   .setRole(user.getRole());
        if (requestDto.getNewPassword() != null && requestDto.getCurrentPassword() != null) {
            if (passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                updatedUser.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            } else {
                throw new RuntimeException("The current password is incorrect");
            }
        } else {
            updatedUser.setPassword(user.getPassword());
        }
        return updatedUser;
    }
    
    private String updateAuthentication(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(user,
                authentication.getCredentials(), authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return jwtUtil.generateToken(user.getUsername());
    }
}
