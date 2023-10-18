package project.carsharing.dto.user;

import lombok.Data;
import project.carsharing.model.User;
import project.carsharing.validation.annotation.EnumValueCheck;

@Data
public class RoleRequestDto {
    @EnumValueCheck(User.Role.class)
    private String role;
}
