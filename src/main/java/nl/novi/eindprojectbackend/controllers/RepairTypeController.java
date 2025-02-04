package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.services.RepairTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repair-types")
public class RepairTypeController {

    @Autowired
    private RepairTypeService repairTypeService;


    @PostMapping
    public ResponseEntity<RepairType> addRepairType(@RequestBody RepairType repairType) {
        RepairType newRepairType = repairTypeService.addRepairType(repairType);
        return ResponseEntity.status(201).body(newRepairType);
    }


    @GetMapping
    public ResponseEntity<List<RepairType>> getAllRepairTypes() {
        List<RepairType> repairTypes = repairTypeService.getAllRepairTypes();
        return ResponseEntity.ok(repairTypes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RepairType> getRepairTypeById(@PathVariable Long id) {
        return repairTypeService.getRepairTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateRepairType(@PathVariable Long id, @RequestBody RepairType repairType) {
        try {
            RepairType updatedRepairType = repairTypeService.updateRepairType(id, repairType);
            return ResponseEntity.ok(updatedRepairType);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchRepairType(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot update repair types.");
        }

        try {
            RepairType updatedRepairType = repairTypeService.patchRepairType(id, updates);
            return ResponseEntity.ok(updatedRepairType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating repair type: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepairType(@PathVariable Long id) {
        repairTypeService.deleteRepairType(id);
        return ResponseEntity.noContent().build();
    }
}
