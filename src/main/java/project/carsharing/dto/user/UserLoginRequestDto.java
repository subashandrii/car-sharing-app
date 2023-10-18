package project.carsharing.dto.user;

import lombok.Data;
import project.carsharing.validation.annotation.Email;
import project.carsharing.validation.annotation.Password;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @Password
    private String password;
}
