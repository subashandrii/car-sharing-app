package project.carsharing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.carsharing.dto.user.UserLoginRequestDto;
import project.carsharing.dto.user.UserLoginResponseDto;
import project.carsharing.dto.user.UserRegistrationRequestDto;
import project.carsharing.dto.user.UserResponseDto;
import project.carsharing.exception.RegistrationException;
import project.carsharing.secure.AuthenticationService;

@Tag(name = "Car management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register/manager")
    UserResponseDto registerForManager(@RequestBody @Valid
                                                   UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return authenticationService.register(requestDto, true);
    }
    
    @PostMapping("/register")
    UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return authenticationService.register(requestDto, false);
    }
    
    @PostMapping("/login")
    UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }
}
