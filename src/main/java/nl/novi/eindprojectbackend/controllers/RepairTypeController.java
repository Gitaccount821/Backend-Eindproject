package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.RepairTypeDto;
import nl.novi.eindprojectbackend.mappers.RepairTypeMapper;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.services.RepairTypeService;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<RepairTypeDto> addRepairType(@RequestBody RepairTypeDto repairTypeDto) {

        if (repairTypeDto.getName() == null || repairTypeDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }
        if (repairTypeDto.getCost() == null || repairTypeDto.getCost() <= 0) {
            throw new BadRequestException("Repair type cost must be greater than zero.");
        }

        RepairType repairType = RepairTypeMapper.toEntity(repairTypeDto);
        RepairType newRepairType = repairTypeService.addRepairType(repairType);
        return ResponseEntity.status(HttpStatus.CREATED).body(RepairTypeMapper.toDto(newRepairType));
    }

    @GetMapping
    public ResponseEntity<List<RepairTypeDto>> getAllRepairTypes() {
        List<RepairTypeDto> repairTypeDtos = repairTypeService.getAllRepairTypes()
                .stream().map(RepairTypeMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(repairTypeDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairTypeDto> getRepairTypeById(@PathVariable Long id) {
        return repairTypeService.getRepairTypeById(id)
                .map(repairType -> ResponseEntity.ok(RepairTypeMapper.toDto(repairType)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRepairType(@PathVariable Long id, @RequestBody RepairTypeDto repairTypeDto) {
        try {

            if (repairTypeDto.getName() == null || repairTypeDto.getName().trim().isEmpty()) {
                throw new BadRequestException("Repair type name cannot be empty.");
            }
            if (repairTypeDto.getCost() == null || repairTypeDto.getCost() <= 0) {
                throw new BadRequestException("Repair type cost must be greater than zero.");
            }

            RepairType updatedRepairType = repairTypeService.updateRepairType(id, RepairTypeMapper.toEntity(repairTypeDto));
            return ResponseEntity.ok(RepairTypeMapper.toDto(updatedRepairType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repair type not found.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchRepairType(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot update repair types.");
        }

        try {

            if (updates.containsKey("name")) {
                String name = (String) updates.get("name");
                if (name == null || name.trim().isEmpty()) {
                    throw new BadRequestException("Repair type name cannot be empty.");
                }
            }

            if (updates.containsKey("cost")) {
                Double cost = (Double) updates.get("cost");
                if (cost == null || cost <= 0) {
                    throw new BadRequestException("Repair type cost must be greater than zero.");
                }
            }

            RepairType updatedRepairType = repairTypeService.patchRepairType(id, updates);
            return ResponseEntity.ok(RepairTypeMapper.toDto(updatedRepairType));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid repair type data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating repair type: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepairType(@PathVariable Long id) {
        repairTypeService.deleteRepairType(id);
        return ResponseEntity.noContent().build();
    }
}
