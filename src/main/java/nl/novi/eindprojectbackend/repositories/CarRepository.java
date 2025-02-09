package nl.novi.eindprojectbackend.repositories;

import nl.novi.eindprojectbackend.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

}