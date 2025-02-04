package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.*;
import nl.novi.eindprojectbackend.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> addCar(@RequestBody CarDto carDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot create cars.");
        }

        User owner = userDetailsService.findUserByUsername(carDto.getOwnerUsername());
        if (owner == null) {
            return ResponseEntity.badRequest().body("Invalid ownerUsername: No such user exists.");
        }

        Car car = new Car();
        car.setCarType(carDto.getCarType());
        car.setOwner(owner);
        car.setRepairRequestDate(carDto.getRepairRequestDate());

        Car savedCar = carService.addCar(car, carDto.getOwnerUsername());
        return ResponseEntity.ok(new CarDto(savedCar));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllCars() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant can only access their own cars.");
        }

        List<Car> cars = carService.getAllCars();
        List<CarDto> carDtos = cars.stream().map(this::convertToCarDto).collect(Collectors.toList());
        return ResponseEntity.ok(carDtos);
    }

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

        return ResponseEntity.ok(convertToCarDto(car));
    }

    @PostMapping(value = "/{carId}/repairs", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CarDto> addRepairToCar(@PathVariable Long carId, @RequestBody RepairDto repairDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MONTEUR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Car car = carService.getCarById(carId).orElseThrow(() -> new IllegalArgumentException("Car not found"));

            RepairType repairType = repairTypeService.getRepairTypeById(repairDto.getRepairTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Repair type not found"));

            Repair repair = new Repair();
            repair.setRepairType(repairType);
            repair.setCar(car);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date repairRequestDate = sdf.parse(repairDto.getRepairRequestDate());
            repair.setRepairRequestDate(repairRequestDate);

            if (repairDto.getRepairDate() != null) {
                Date repairDate = sdf.parse(repairDto.getRepairDate());
                repair.setRepairDate(repairDate);
            }

            double totalCost = repairType.getCost();

            if (repairDto.getPartIds() != null && !repairDto.getPartIds().isEmpty()) {
                List<Part> parts = repairDto.getPartIds().stream()
                        .map(partId -> partService.getPartById(partId)
                                .orElseThrow(() -> new IllegalArgumentException("Part not found for ID: " + partId)))
                        .collect(Collectors.toList());

                repair.setParts(parts);

                for (Part part : parts) {
                    totalCost += part.getPrice();
                    part.setStock(part.getStock() - 1);
                    partService.updatePart(part);
                }
            }

            repair.setTotalRepairCost(totalCost);
            repairService.addRepair(repair);
            car.getRepairs().add(repair);
            car.updateTotalRepairCost();
            carService.updateCar(car.getId(), car);

            return ResponseEntity.ok(convertToCarDto(car));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value = "/{carId}/repairs", produces = "application/json")
    public ResponseEntity<?> getRepairsByCarId(@PathVariable Long carId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Car car = carService.getCarById(carId).orElse(null);
        if (car == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found.");
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            String username = auth.getName();
            if (!car.getOwner().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only access your own car.");
            }
        }

        List<RepairDto> repairDtos = car.getRepairs().stream()
                .map(RepairDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(repairDtos);
    }

    @PatchMapping("/{carId}/repairs/{repairId}")
    public ResponseEntity<?> patchRepair(
            @PathVariable Long carId,
            @PathVariable Long repairId,
            @RequestBody Map<String, Object> updates) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MONTEUR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Monteur can update repairs.");
        }

        try {
            Repair updatedRepair = repairService.patchRepair(carId, repairId, updates);
            return ResponseEntity.ok(new RepairDto(updatedRepair));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating repair: " + e.getMessage());
        }
    }


    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateCar(@PathVariable Long id, @RequestBody CarDto carDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot update cars.");
        }

        Car updatedCar = carService.updateCar(id, carDto);
        return ResponseEntity.ok(new CarDto(updatedCar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MONTEUR") || a.getAuthority().equals("ROLE_MEDEWERKER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to delete this car.");
        }

        try {
            carService.deleteCar(id);
            return ResponseEntity.ok("Car deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found.");
        }
    }

    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> patchCar(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot update cars.");
        }

        try {
            Car updatedCar = carService.patchCar(id, updates);
            return ResponseEntity.ok(new CarDto(updatedCar));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating car: " + e.getMessage());
        }
    }





    private CarDto convertToCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getCarType(),
                car.getOwner().getUsername(),
                car.getRepairs() != null ? car.getRepairs().stream()
                        .map(repair -> new RepairDto(
                                repair.getId(),
                                repair.getRepairType().getId(),
                                repair.getRepairType().getName(),
                                repair.getRepairType().getCost(),
                                repair.getTotalRepairCost(),
                                new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairRequestDate()),
                                repair.getRepairDate() != null ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairDate()) : null,
                                repair.getParts() != null ? repair.getParts().stream().map(Part::getId).collect(Collectors.toList()) : null,
                                repair.getParts() != null ? repair.getParts().stream()
                                        .map(part -> new PartDetailDto(part.getId(), part.getName(), part.getPrice()))
                                        .collect(Collectors.toList()) : null
                        ))
                        .collect(Collectors.toList()) : List.of(),
                car.getTotalRepairCost(),
                car.getRepairRequestDate()
        );
    }


}
