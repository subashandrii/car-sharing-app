package project.carsharing.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserUpdateResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
}
