package project.carsharing.dto.user;

import lombok.Data;
import project.carsharing.validation.annotation.Email;
import project.carsharing.validation.annotation.FieldsValueMatch;
import project.carsharing.validation.annotation.Name;
import project.carsharing.validation.annotation.Password;

@FieldsValueMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "passwords do not match!")
@Data
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @Name
    private String firstName;
    @Name
    private String lastName;
    @Password
    private String password;
    private String repeatPassword;
}
