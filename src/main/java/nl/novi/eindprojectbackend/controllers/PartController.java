package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    @Autowired
    private PartService partService;

    @GetMapping
    public List<Part> getAllParts() {
        return partService.getAllParts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return partService.getPartById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Part> addPart(@RequestBody Part part) {
        return ResponseEntity.status(201).body(partService.addPart(part));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Part> updatePart(@PathVariable Long id, @RequestBody Part part) {
        return ResponseEntity.ok(partService.updatePart(id, part));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
