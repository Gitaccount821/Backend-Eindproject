package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.services.PartService;

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
    public ResponseEntity<PartDetailDto> addPart(@Valid @RequestBody PartDetailDto partDetailDto) {
        PartDetailDto savedPart = partService.addPart(partDetailDto);
        return ResponseEntity.ok(savedPart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartDetailDto> getPartById(@PathVariable Long id) {
        PartDetailDto part = partService.getPartById(id);
        return ResponseEntity.ok(part);
    }

    @GetMapping
    public ResponseEntity<List<PartDetailDto>> getAllParts() {
        List<PartDetailDto> parts = partService.getAllParts();
        return ResponseEntity.ok(parts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartDetailDto> updatePart(@PathVariable Long id, @Valid @RequestBody PartDetailDto partDetailDto) {
        PartDetailDto updatedPart = partService.updatePart(id, partDetailDto);
        return ResponseEntity.ok(updatedPart);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PartDetailDto> patchPart(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        PartDetailDto updatedPart = partService.patchPart(id, updates);
        return ResponseEntity.ok(updatedPart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.ok("Part deleted successfully.");
    }
}
