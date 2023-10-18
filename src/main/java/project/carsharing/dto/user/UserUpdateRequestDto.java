package project.carsharing.dto.user;

import lombok.Data;
import project.carsharing.validation.annotation.Email;
import project.carsharing.validation.annotation.FieldsValueMatch;
import project.carsharing.validation.annotation.FieldsValueNotMatch;
import project.carsharing.validation.annotation.Name;
import project.carsharing.validation.annotation.Password;

@FieldsValueMatch(
        field = "newPassword",
        fieldMatch = "repeatNewPassword",
        message = "passwords do not match!")
@FieldsValueNotMatch(
        field = "currentPassword",
        fieldNotMatch = "newPassword",
        message = "current and new passwords must not match!")
@Data
public class UserUpdateRequestDto {
    @Email
    private String email;
    @Name
    private String firstName;
    @Name
    private String lastName;
    @Password(nullable = true)
    private String currentPassword;
    @Password(nullable = true)
    private String newPassword;
    private String repeatNewPassword;
}
