package project.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.carsharing.dto.user.RoleRequestDto;
import project.carsharing.dto.user.UserResponseDto;
import project.carsharing.dto.user.UserUpdateRequestDto;
import project.carsharing.dto.user.UserUpdateResponseDto;
import project.carsharing.secure.AuthenticationService;
import project.carsharing.service.UserService;

@Tag(name = "User management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    
    @GetMapping("/me")
    @Operation(summary = "Get data about my profile")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }
    
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user`s role")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public UserResponseDto updateUsersRole(
            @PathVariable Long id,
            @RequestBody @Valid RoleRequestDto requestDto,
            Authentication authentication) {
        return userService.updateUsersRole(id, requestDto, authentication.getName());
    }
    
    @PutMapping("/me")
    @Operation(summary = "Update profile info")
    public UserUpdateResponseDto updateProfileInfo(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return authenticationService.updateProfileInfo(authentication.getName(), requestDto);
    }
}
