package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.exceptions.*;
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

    private String getUserRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("Unknown Role");
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDto carDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = getUserRole(auth);

        if (userRole.equals("KLANT")) {
            throw new ForbiddenActionException(userRole, "may not create cars");
        }

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userRole = getUserRole(auth);

        if (userRole.equals("KLANT")) {
            throw new ForbiddenActionException(userRole, "can only access their own cars", true);
        }

        List<CarDto> carDtos = carService.getAllCars().stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(carDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        Car car = carService.getCarById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = getUserRole(auth);

        if (userRole.equals("KLANT")) {
            String username = auth.getName();
            if (!car.getOwner().getUsername().equals(username)) {
                throw new ForbiddenActionException(userRole, "can only access their own cars", true);
            }
        }

        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    @PostMapping(value = "/{carId}/repairs", consumes = "application/json", produces = "application/json")
    public Object addRepairToCar(@PathVariable Long carId, @RequestBody RepairDto repairDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MONTEUR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            Car car = carService.getCarById(carId)
                    .orElseThrow(() -> new RecordNotFoundException("Car", carId));

            if (repairDto.getRepairTypeId() == null) {
                throw new BadRequestException("Repair type ID is required.");
            }

            RepairType repairType = repairTypeService.getRepairTypeById(repairDto.getRepairTypeId())
                    .orElseThrow(() -> new RecordNotFoundException("Repair Type", repairDto.getRepairTypeId()));

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
                throw new BadRequestException("Repair Request Date", true);
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
                throw new BadRequestException("Repair date", true);
            }

            double totalCost = repairType.getCost();

            if (repairDto.getPartIds() != null && !repairDto.getPartIds().isEmpty()) {
                repair.setParts(repairDto.getPartIds().stream()
                        .map(partId -> partService.getPartById(partId)
                                .orElseThrow(() -> new RecordNotFoundException("Part", partId)))
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
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Car does not exist");
        }
    }

    @PatchMapping("/{carId}/repairs/{repairId}")
    public ResponseEntity<?> patchRepair(
            @PathVariable Long carId,
            @PathVariable Long repairId,
            @RequestBody Map<String, Object> updates) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MONTEUR"))) {
            throw new ForbiddenActionException(getUserRole(auth), "update repairs");
        }

        try {
            Repair updatedRepair = repairService.patchRepair(carId, repairId, updates);
            return ResponseEntity.ok(new RepairDto(updatedRepair));
        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Repair", repairId);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Error updating repair.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchCar(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            throw new ForbiddenActionException(getUserRole(auth), "update cars");
        }

        try {
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
                try {
                    car.setRepairRequestDate(String.valueOf(sdf.parse(repairRequestDate)));
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

            carService.updateCar(car.getId(), car);
            return ResponseEntity.ok(CarMapper.toDto(car));
        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Car", id);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Error updating car.");
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = getUserRole(auth);

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_MONTEUR") || a.getAuthority().equals("ROLE_MEDEWERKER"))) {
            throw new ForbiddenActionException(userRole, "delete cars");
        }

        carService.getCarById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully.");
    }
}
