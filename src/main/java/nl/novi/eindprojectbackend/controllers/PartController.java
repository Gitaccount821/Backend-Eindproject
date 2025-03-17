package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.PartService;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<Part> addPart(@Valid @RequestBody Part part) {
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
    public ResponseEntity<List<Part>> getAllParts() {
        List<Part> parts = partService.getAllParts();
        return ResponseEntity.ok(parts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        Part updatedPart = partService.updatePart(id, partDetails);
        return ResponseEntity.ok(updatedPart);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Part> patchPart(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Part updatedPart = partService.patchPart(id, updates);
        return ResponseEntity.ok(updatedPart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.ok("Part deleted successfully.");
    }
}

