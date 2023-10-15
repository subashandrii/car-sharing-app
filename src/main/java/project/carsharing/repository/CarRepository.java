package project.carsharing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.carsharing.model.Car;

public interface CarRepository extends JpaRepository<Car, Long> {

}
