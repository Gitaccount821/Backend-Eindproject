package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.CarNotFoundException;
import nl.novi.eindprojectbackend.exceptions.RepairTypeNotFoundException;
import nl.novi.eindprojectbackend.mappers.CarMapper;
import nl.novi.eindprojectbackend.models.*;
import nl.novi.eindprojectbackend.services.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final RepairService repairService;

    private final PartService partService;

    private final RepairTypeService repairTypeService;

    private final CustomUserDetailsService userDetailsService;

    public CarController(CarService carService, RepairService repairService, PartService partService, RepairTypeService repairTypeService, CustomUserDetailsService userDetailsService) {
        this.carService = carService;
        this.repairService = repairService;
        this.partService = partService;
        this.repairTypeService = repairTypeService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDto carDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot create cars.");
        }

        try {
            User owner = userDetailsService.findUserByUsername(carDto.getOwnerUsername());
            if (owner == null) {
                throw new IllegalArgumentException("Invalid ownerUsername: No such user exists.");
            }

            Car car = new Car();
            car.setCarType(carDto.getCarType());
            car.setOwner(owner);
            car.setRepairRequestDate(carDto.getRepairRequestDate());

            Car savedCar = carService.addCar(car, carDto.getOwnerUsername());
            return ResponseEntity.ok(CarMapper.toDto(savedCar));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding car: " + e.getMessage());
        }
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllCars() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant can only access their own cars.");
        }

        List<CarDto> carDtos = carService.getAllCars().stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(carDtos);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            Car car = carService.getCarById(id).orElseThrow(() -> new CarNotFoundException("Car not found"));

            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
                String username = auth.getName();
                if (!car.getOwner().getUsername().equals(username)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only access your own car.");
                }
            }

            return ResponseEntity.ok(CarMapper.toDto(car));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found for id: " + id);
        }
    }

    @PostMapping(value = "/{carId}/repairs", consumes = "application/json", produces = "application/json")
    public Object addRepairToCar(@PathVariable Long carId, @RequestBody RepairDto repairDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MONTEUR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Car car = carService.getCarById(carId).orElseThrow(() -> new CarNotFoundException("Car not found"));

            if (repairDto.getRepairTypeId() == null) {
                throw new BadRequestException("Repair type ID is required.");
            }

            RepairType repairType = repairTypeService.getRepairTypeById(repairDto.getRepairTypeId())
                    .orElseThrow(() -> new RepairTypeNotFoundException("Repair type not found"));

            Repair repair = new Repair();
            repair.setRepairType(repairType);
            repair.setCar(car);

            String repairRequestDate = repairDto.getRepairRequestDate();
            if (repairRequestDate != null && !repairRequestDate.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    repair.setRepairRequestDate(sdf.parse(repairRequestDate));
                } catch (Exception e) {
                    throw new BadRequestException("Invalid repair request date format. Use dd-MM-yyyy.");
                }
            } else {
                throw new BadRequestException("Repair request date cannot be empty.");
            }

            String repairDate = repairDto.getRepairDate();
            if (repairDate != null && !repairDate.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    repair.setRepairDate(sdf.parse(repairDate));
                } catch (Exception e) {
                    throw new BadRequestException("Invalid repair date format. Use dd-MM-yyyy.");
                }
            } else {
                throw new BadRequestException("Repair date cannot be empty.");
            }

            double totalCost = repairType.getCost();

            if (repairDto.getPartIds() != null && !repairDto.getPartIds().isEmpty()) {
                repair.setParts(repairDto.getPartIds().stream()
                        .map(partId -> partService.getPartById(partId)
                                .orElseThrow(() -> new BadRequestException("Part not found for ID: " + partId)))
                        .collect(Collectors.toList()));

                for (Part part : repair.getParts()) {
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

            return ResponseEntity.ok(CarMapper.toDto(car));
        } catch (BadRequestException | RepairTypeNotFoundException | CarNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PatchMapping("/{carId}/repairs/{repairId}")
    public ResponseEntity<?> patchRepair(
            @PathVariable Long carId,
            @PathVariable Long repairId,
            @RequestBody Map<String, Object> updates) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MONTEUR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Monteur can update repairs.");
        }

        try {
            Repair updatedRepair = repairService.patchRepair(carId, repairId, updates);
            return ResponseEntity.ok(new RepairDto(updatedRepair));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating repair: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchCar(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot update cars.");
        }

        try {
            Car car = carService.getCarById(id).orElseThrow(() -> new IllegalArgumentException("Car not found"));

            if (updates.containsKey("carType")) {
                car.setCarType((String) updates.get("carType"));
            }

            if (updates.containsKey("repairRequestDate")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String repairRequestDate = (String) updates.get("repairRequestDate");
                car.setRepairRequestDate(String.valueOf(sdf.parse(repairRequestDate)));
            }

            carService.updateCar(car.getId(), car);

            return ResponseEntity.ok(CarMapper.toDto(car));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating car: " + e.getMessage());
        }
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
}
