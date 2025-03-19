package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.RepairTypeDto;
import nl.novi.eindprojectbackend.mappers.RepairTypeMapper;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.services.RepairTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repair-types")
public class RepairTypeController {

    private final RepairTypeService repairTypeService;

    public RepairTypeController(RepairTypeService repairTypeService) {
        this.repairTypeService = repairTypeService;
    }

    @PostMapping
    public ResponseEntity<RepairTypeDto> addRepairType(@Valid @RequestBody RepairTypeDto repairTypeDto) {
        RepairType repairType = RepairTypeMapper.toEntity(repairTypeDto);
        RepairType savedRepairType = repairTypeService.addRepairType(repairType);
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairTypeMapper.toDto(savedRepairType));
    }

    @GetMapping
    public ResponseEntity<List<RepairTypeDto>> getAllRepairTypes() {
        List<RepairTypeDto> dtos = repairTypeService.getAllRepairTypes().stream()
                .map(RepairTypeMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairTypeDto> getRepairTypeById(@PathVariable Long id) {
        RepairType repairType = repairTypeService.getRepairTypeById(id);
        return ResponseEntity.ok(RepairTypeMapper.toDto(repairType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepairTypeDto> updateRepairType(@PathVariable Long id,
                                                          @Valid @RequestBody RepairTypeDto repairTypeDto) {
        RepairType updatedRepairType = repairTypeService.updateRepairType(id, RepairTypeMapper.toEntity(repairTypeDto));
        return ResponseEntity.ok(RepairTypeMapper.toDto(updatedRepairType));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RepairTypeDto> patchRepairType(@PathVariable Long id,
                                                         @RequestBody Map<String, Object> updates) {
        RepairType patchedRepairType = repairTypeService.patchRepairType(id, updates);
        return ResponseEntity.ok(RepairTypeMapper.toDto(patchedRepairType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepairType(@PathVariable Long id) {
        repairTypeService.deleteRepairType(id);
        return ResponseEntity.noContent().build();
    }
}
