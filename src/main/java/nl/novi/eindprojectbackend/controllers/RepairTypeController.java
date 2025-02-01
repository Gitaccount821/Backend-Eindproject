package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.services.RepairTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<RepairType> updateRepairType(@PathVariable Long id, @RequestBody RepairType repairType) {
        return repairTypeService.updateRepairType(id, repairType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepairType(@PathVariable Long id) {
        repairTypeService.deleteRepairType(id);
        return ResponseEntity.noContent().build();
    }
}
