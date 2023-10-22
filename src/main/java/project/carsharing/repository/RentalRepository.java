package project.carsharing.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.carsharing.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByActualReturnDateIsNull(Pageable pageable);
    
    List<Rental> findAllByActualReturnDateIsNotNull(Pageable pageable);
    
    List<Rental> findAllByUserIdAndActualReturnDateIsNull(Long id, Pageable pageable);
    
    List<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Long id, Pageable pageable);
    
    List<Rental> findAllByUserEmail(String email, Pageable pageable);
}
