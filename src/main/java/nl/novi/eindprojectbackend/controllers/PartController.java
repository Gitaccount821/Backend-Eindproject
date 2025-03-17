package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.PartService;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.utils.ValidationUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<?> addPart(@Valid @RequestBody Part part) {

        ValidationUtils.validateNotEmpty(part.getName(), "Part name");
        ValidationUtils.validatePositiveNumber(part.getPrice(), "Part price");
        ValidationUtils.validateNonNegativeNumber(part.getStock(), "Part stock");

        Part savedPart = partService.addPart(part);
        return ResponseEntity.ok(savedPart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {

        Part part = partService.getPartById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        return ResponseEntity.ok(part);
    }

    @GetMapping
    public ResponseEntity<?> getAllParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {

        partService.getPartById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        ValidationUtils.validateNotEmpty(partDetails.getName(), "Part name");
        ValidationUtils.validatePositiveNumber(partDetails.getPrice(), "Part price");
        ValidationUtils.validateNonNegativeNumber(partDetails.getStock(), "Part stock");

        Part updatedPart = partService.updatePart(id, partDetails);
        return ResponseEntity.ok(updatedPart);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchPart(@PathVariable Long id, @RequestBody Map<String, Object> updates) {

        partService.getPartById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        if (updates.containsKey("name")) {
            ValidationUtils.validateNotEmpty((String) updates.get("name"), "Part name");
        }

        if (updates.containsKey("price")) {
            ValidationUtils.validatePositiveNumber(updates.get("price"), "Part price");
        }

        if (updates.containsKey("stock")) {
            ValidationUtils.validateNonNegativeNumber(updates.get("stock"), "Part stock");
        }

        Part updatedPart = partService.patchPart(id, updates);
        return ResponseEntity.ok(updatedPart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePart(@PathVariable Long id) {

        partService.getPartById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        partService.deletePart(id);
        return ResponseEntity.ok("Part deleted successfully.");
    }
}
