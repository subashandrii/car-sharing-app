package project.carsharing.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import project.carsharing.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    User getUserByEmail(String email);
    
    Optional<User> findByTelegramChatId(Long telegramChatId);
    
    List<User> findAllByTelegramChatIdIsNotNull();
}
