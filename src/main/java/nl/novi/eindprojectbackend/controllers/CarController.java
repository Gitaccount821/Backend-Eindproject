package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.mappers.CarMapper;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.services.CarService;
import nl.novi.eindprojectbackend.services.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
@RestController
@RequestMapping(value = "/api/cars", produces = "application/json")
public class CarController {

    private final CarService carService;
    private final CustomUserDetailsService userDetailsService;

    public CarController(CarService carService, CustomUserDetailsService userDetailsService) {
        this.carService = carService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<CarDto> addCar(@Valid @RequestBody CarDto carDto) {
        Car car = new Car();
        car.setCarType(carDto.getCarType());
        car.setRepairRequestDate(carDto.getRepairRequestDate());

        Car savedCar = carService.addCar(car, carDto.getOwnerUsername());
        return ResponseEntity.ok(CarMapper.toDto(savedCar));
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> carDtos = carService.getAllCars().stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(carDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CarDto> patchCar(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Car patchedCar = carService.patchCar(id, updates);
        return ResponseEntity.ok(CarMapper.toDto(patchedCar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully.");
    }
}
