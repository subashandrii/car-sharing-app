package project.carsharing.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.carsharing.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByActualReturnDateIsNull(Pageable pageable);
    
    List<Rental> findAllByActualReturnDateIsNotNull(Pageable pageable);
    
    List<Rental> findAllByUserIdAndActualReturnDateIsNull(Long id, Pageable pageable);
    
    List<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Long id, Pageable pageable);
    
    List<Rental> findAllByUserEmail(String email, Pageable pageable);
    
    @Query("FROM Rental r "
                   + "JOIN FETCH r.user "
                   + "WHERE r.returnDate < ?1 AND r.actualReturnDate IS NULL "
                   + "ORDER BY r.returnDate DESC")
    List<Rental> findAllByReturnDateBefore(LocalDate currentDate);
}
