package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.services.CarService;
import nl.novi.eindprojectbackend.services.CustomUserDetailsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/cars", produces = "application/json")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService, CustomUserDetailsService userDetailsService) {
        this.carService = carService;
    }


    @PostMapping(consumes = "application/json")
    public ResponseEntity<CarDto> addCar(@Valid @RequestBody CarDto carDto) {
        CarDto savedCar = carService.addCar(carDto);
        return ResponseEntity.ok(savedCar);
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> carDtos = carService.getAllCars();
        return ResponseEntity.ok(carDtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        CarDto carDto = carService.getCarById(id);
        return ResponseEntity.ok(carDto);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<CarDto> patchCar(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        CarDto patchedCar = carService.patchCar(id, updates);
        return ResponseEntity.ok(patchedCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully.");
    }
}
