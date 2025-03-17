package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.exceptions.*;
import nl.novi.eindprojectbackend.models.*;
import nl.novi.eindprojectbackend.services.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/repairs", produces = "application/json")
public class RepairController {

    private final RepairService repairService;
    private final CarService carService;
    private final PartService partService;
    private final RepairTypeService repairTypeService;

    public RepairController(RepairService repairService, CarService carService, PartService partService, RepairTypeService repairTypeService) {
        this.repairService = repairService;
        this.carService = carService;
        this.partService = partService;
        this.repairTypeService = repairTypeService;
    }


    @PostMapping("/{carId}")
    public ResponseEntity<?> addRepairToCar(@PathVariable Long carId, @RequestBody RepairDto repairDto) {
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

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                repair.setRepairRequestDate(sdf.parse(repairDto.getRepairRequestDate()));
                repair.setRepairDate(sdf.parse(repairDto.getRepairDate()));
            } catch (Exception e) {
                throw new BadRequestException("Invalid date format. Use dd-MM-yyyy.");
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
                    partService.updatePart(part.getId(), part);
                }
            }

            repair.setTotalRepairCost(totalCost);
            repairService.addRepair(repair);
            car.getRepairs().add(repair);
            car.updateTotalRepairCost();
            carService.updateCar(car.getId(), car);

            return ResponseEntity.ok(new RepairDto(repair));
        } catch (RecordNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Unexpected internal error occurred.");
        }

    }

    @PatchMapping("/{carId}/{repairId}")
    public ResponseEntity<?> patchRepair(@PathVariable Long carId, @PathVariable Long repairId, @RequestBody Map<String, Object> updates) {
        try {
            Repair updatedRepair = repairService.patchRepair(carId, repairId, updates);
            return ResponseEntity.ok(new RepairDto(updatedRepair));
        } catch (RecordNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Unexpected internal error occurred.");
        }
    }
}
