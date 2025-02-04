package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.PartService;
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

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_KLANT"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Klant cannot update parts.");
        }

        Part updatedPart = partService.updatePart(id, partDetails);
        return ResponseEntity.ok(updatedPart);
    }


    @PostMapping
    public ResponseEntity<Part> addPart(@RequestBody Part part) {
        return ResponseEntity.status(201).body(partService.addPart(part));
    }

    @PutMapping
    public ResponseEntity<Part> updatePart(@RequestBody Part part) {
        if (part.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(partService.updatePart(part));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Part> patchPart(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Part part = partService.getPartById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part not found"));

        if (updates.containsKey("price")) {
            part.setPrice(((Number) updates.get("price")).doubleValue());
        }
        if (updates.containsKey("stock")) {
            part.setStock(((Number) updates.get("stock")).intValue());
        }
        if (updates.containsKey("name")) {
            part.setName((String) updates.get("name"));
        }

        Part updatedPart = partService.updatePart(part);
        return ResponseEntity.ok(updatedPart);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
