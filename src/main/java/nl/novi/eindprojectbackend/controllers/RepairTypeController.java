package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.RepairTypeDto;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.mappers.RepairTypeMapper;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.services.RepairTypeService;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.utils.ValidationUtils;
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

        ValidationUtils.validateNotEmpty(repairTypeDto.getName(), "Repair type name");
        ValidationUtils.validatePositiveNumber(repairTypeDto.getCost(), "Repair type cost");

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
        RepairType repairType = repairTypeService.getRepairTypeById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));

        return ResponseEntity.ok(RepairTypeMapper.toDto(repairType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepairTypeDto> updateRepairType(@PathVariable Long id, @RequestBody RepairTypeDto repairTypeDto) {

        ValidationUtils.validateNotEmpty(repairTypeDto.getName(), "Repair type name");
        ValidationUtils.validatePositiveNumber(repairTypeDto.getCost(), "Repair type cost");

        repairTypeService.getRepairTypeById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));

        RepairType updatedRepairType = repairTypeService.updateRepairType(id, RepairTypeMapper.toEntity(repairTypeDto));
        return ResponseEntity.ok(RepairTypeMapper.toDto(updatedRepairType));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RepairTypeDto> patchRepairType(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            throw new BadRequestException("Klant cannot update repair types.");
        }

        repairTypeService.getRepairTypeById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));

        String name = updates.containsKey("name") ? (String) updates.get("name") : null;
        Object costObj = updates.get("cost");

        if (name != null) {
            ValidationUtils.validateNotEmpty(name, "Repair type name");
        }

        if (costObj != null) {
            ValidationUtils.validatePositiveNumber(costObj, "Repair type cost");
        }

        if (!updates.containsKey("description")) {
            updates.put("description", repairTypeService.getRepairTypeById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Repair Type", id))
                    .getDescription());
        }

        RepairType updatedRepairType = repairTypeService.patchRepairType(id, updates);
        return ResponseEntity.ok(RepairTypeMapper.toDto(updatedRepairType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepairType(@PathVariable Long id) {
        repairTypeService.getRepairTypeById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));

        repairTypeService.deleteRepairType(id);
        return ResponseEntity.noContent().build();
    }

}
