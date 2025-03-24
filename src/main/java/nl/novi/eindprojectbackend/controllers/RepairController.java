package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.mappers.RepairMapper;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.services.RepairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/repairs", produces = "application/json")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @PostMapping("/{carId}")
    public ResponseEntity<RepairDto> addRepairToCar(@PathVariable Long carId,
                                                    @Valid @RequestBody RepairDto repairDto) {
        Repair repair = repairService.addRepairToCar(carId, repairDto);

        return ResponseEntity.ok(RepairMapper.toDto(repair));
    }

    @PatchMapping("/{carId}/{repairId}")
    public ResponseEntity<RepairDto> patchRepair(@PathVariable Long carId,
                                                 @PathVariable Long repairId,
                                                 @RequestBody Map<String, Object> updates) {
        Repair updatedRepair = repairService.patchRepair(carId, repairId, updates);

        return ResponseEntity.ok(RepairMapper.toDto(updatedRepair));
    }
}
