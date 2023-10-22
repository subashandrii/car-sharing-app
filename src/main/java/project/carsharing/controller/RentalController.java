package project.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.carsharing.dto.rental.RentalCreateResponseDto;
import project.carsharing.dto.rental.RentalRequestDto;
import project.carsharing.dto.rental.RentalResponseDto;
import project.carsharing.service.RentalService;

@Tag(name = "Rental management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a rental by id")
    public RentalResponseDto getRental(@PathVariable Long id, Authentication authentication) {
        return rentalService.findById(authentication.getName(), id);
    }
    
    @GetMapping
    @Operation(summary = "Get all user`s rentals")
    public List<RentalResponseDto> getRentalsByUser(Authentication authentication,
                                                    Pageable pageable) {
        return rentalService.getAllByUser(authentication.getName(), pageable);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Get rentals by user id")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<RentalResponseDto> findAllIfTheyAreActive(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active") boolean isActive,
            Pageable pageable
    ) {
        return rentalService.findAllIfTheyAreActive(userId, isActive, pageable);
    }
    
    @PostMapping
    @Operation(summary = "Create a new rental")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalCreateResponseDto createRental(@RequestBody @Valid RentalRequestDto requestDto,
                                                Authentication authentication) {
        return rentalService.create(authentication.getName(), requestDto);
    }
    
    @PostMapping("/{id}/return")
    @Operation(summary = "Return car after rental and set actual return date")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public RentalResponseDto returnCarAfterRental(@PathVariable Long id) {
        return rentalService.returnCarAfterRental(id);
    }
}




