package project.carsharing.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.carsharing.dto.rental.RentalCreateResponseDto;
import project.carsharing.dto.rental.RentalRequestDto;
import project.carsharing.dto.rental.RentalResponseDto;

public interface RentalService {
    RentalResponseDto findById(String email, Long id);
    
    List<RentalResponseDto> getAllByUser(String email, Pageable pageable);
    
    List<RentalResponseDto> findAllIfTheyAreActive(Long userId, boolean isActive,
                                                   Pageable pageable);
    
    RentalCreateResponseDto create(RentalRequestDto requestDto, String email);
    
    RentalResponseDto returnCarAfterRental(Long id, String email);
}
