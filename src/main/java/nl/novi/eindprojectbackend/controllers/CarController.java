package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.*;
import nl.novi.eindprojectbackend.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/cars", produces = "application/json")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private PartService partService;

    @Autowired
    private RepairTypeService repairTypeService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * ✅ Monteur & Medewerker can add cars.
     * ❌ Klant cannot create cars (returns 403 Forbidden).
     */

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> addCar(@RequestBody CarDto carDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("🔍 Request received to add car. User: " + auth.getName());
        auth.getAuthorities().forEach(role -> System.out.println("✅ User Role: " + role.getAuthority()));

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            System.out.println("❌ Denied: Klant cannot create cars.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot create cars.");
        }

        System.out.println("🔍 Looking up owner: " + carDto.getOwnerUsername());

        User owner;
        try {
            owner = userDetailsService.findUserByUsername(carDto.getOwnerUsername());
            System.out.println("✅ Found owner: " + owner.getUsername());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid ownerUsername: No such user exists.");
        }

        Car car = new Car();
        car.setCarType(carDto.getCarType());
        car.setOwner(owner);
        car.setRepairRequestDate(carDto.getRepairRequestDate());

        System.out.println("✅ Saving car: " + car.getCarType() + " for owner: " + car.getOwner().getUsername());

        Car savedCar = carService.addCar(car, carDto.getOwnerUsername());

        return ResponseEntity.ok(new CarDto(savedCar));
    }




    /**
     * ✅ Monteur & Medewerker can view all cars.
     * ❌ Klant cannot access all cars (returns 403 Forbidden).
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllCars() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant can only access their own cars.");
        }

        List<Car> cars = carService.getAllCars();
        List<CarDto> carDtos = cars.stream().map(CarDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(carDtos);
    }

    /**
     * ✅ Monteur & Medewerker can see any car.
     * ✅ Klant can only see **their** car.
     * ❌ Klant trying to see **other cars** → 403 Forbidden.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Car car = carService.getCarById(id).orElse(null);

        if (car == null) {
            return ResponseEntity.notFound().build();
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            String username = auth.getName();
            if (!car.getOwner().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only access your own car.");
            }
        }

        return ResponseEntity.ok(new CarDto(car));
    }
}
