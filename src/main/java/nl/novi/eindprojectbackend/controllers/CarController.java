package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.exceptions.*;
import nl.novi.eindprojectbackend.mappers.CarMapper;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.services.CarService;
import nl.novi.eindprojectbackend.services.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDto carDto) {

        User owner = userDetailsService.findUserByUsername(carDto.getOwnerUsername());
        if (owner == null) {
            throw new RecordNotFoundException("User", null);
        }

        if (carDto.getCarType() == null || carDto.getCarType().trim().isEmpty()) {
            throw new BadRequestException("Car type", true);
        }

        if (carDto.getRepairRequestDate() == null || carDto.getRepairRequestDate().trim().isEmpty()) {
            throw new BadRequestException("Repair request date", true);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);

        try {
            sdf.parse(carDto.getRepairRequestDate());
        } catch (Exception e) {
            throw new BadRequestException("Invalid repair request date format. Use dd-MM-yyyy.");
        }

        Car car = new Car();
        car.setCarType(carDto.getCarType());
        car.setOwner(owner);
        car.setRepairRequestDate(carDto.getRepairRequestDate());

        Car savedCar = carService.addCar(car, carDto.getOwnerUsername());
        return ResponseEntity.ok(CarMapper.toDto(savedCar));
    }

    @GetMapping
    public ResponseEntity<?> getAllCars() {
        List<CarDto> carDtos = carService.getAllCars().stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(carDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchCar(@PathVariable Long id, @RequestBody Map<String, Object> updates) {

        Car car = carService.getCarById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        if (updates.containsKey("carType")) {
            String carType = (String) updates.get("carType");
            if (carType == null || carType.trim().isEmpty()) {
                throw new BadRequestException("Car type", true);
            }
            car.setCarType(carType);
        }

        if (updates.containsKey("repairRequestDate")) {
            String repairRequestDate = (String) updates.get("repairRequestDate");
            if (repairRequestDate == null || repairRequestDate.trim().isEmpty()) {
                throw new BadRequestException("Repair request date", true);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            try {
                sdf.parse(repairRequestDate);
                car.setRepairRequestDate(repairRequestDate);
            } catch (Exception e) {
                throw new BadRequestException("Invalid repair request date format. Use dd-MM-yyyy.");
            }
        }

        if (updates.containsKey("ownerUsername")) {
            String ownerUsername = (String) updates.get("ownerUsername");
            User owner = userDetailsService.findUserByUsername(ownerUsername);
            if (owner == null) {
                throw new RecordNotFoundException("User", null);
            }
            car.setOwner(owner);
        }

        Car updatedCar = carService.updateCar(car.getId(), car);
        return ResponseEntity.ok(CarMapper.toDto(updatedCar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {

        carService.getCarById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully.");
    }
}
