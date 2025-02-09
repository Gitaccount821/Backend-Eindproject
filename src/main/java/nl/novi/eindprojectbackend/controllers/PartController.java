package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.PartService;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
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
        try {
            if (part.getPrice() == null || part.getPrice() < 0) {
                throw new BadRequestException("Price must be a positive value.");
            }
            if (part.getStock() == null || part.getStock() < 0) {
                throw new BadRequestException("Stock must be a non-negative value.");
            }

            Part savedPart = partService.addPart(part);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPart);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid part data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding part: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return partService.getPartById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> getAllParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        try {

            if (partDetails.getPrice() == null || partDetails.getPrice() < 0) {
                throw new BadRequestException("Price must be a positive value.");
            }

            if (partDetails.getStock() == null || partDetails.getStock() < 0) {
                throw new BadRequestException("Stock must be a non-negative value.");
            }

            if (partDetails.getName() == null || partDetails.getName().trim().isEmpty()) {
                throw new BadRequestException("Part name cannot be empty.");
            }

            Part updatedPart = partService.updatePart(id, partDetails);
            return ResponseEntity.ok(updatedPart);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid part data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating part: " + e.getMessage());
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> patchPart(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {

            if (updates.containsKey("price")) {
                Double price = (Double) updates.get("price");
                if (price == null || price < 0) {
                    throw new BadRequestException("Price must be a positive value.");
                }
            }
            if (updates.containsKey("stock")) {
                Integer stock = (Integer) updates.get("stock");
                if (stock == null || stock < 0) {
                    throw new BadRequestException("Stock must be a non-negative value.");
                }
            }


            if (updates.containsKey("name")) {
                String name = (String) updates.get("name");
                if (name == null || name.trim().isEmpty()) {
                    throw new BadRequestException("Part name cannot be empty.");
                }
            }


            Part updatedPart = partService.patchPart(id, updates);
            return ResponseEntity.ok(updatedPart);

        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid part data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating part: " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePart(@PathVariable Long id) {
        try {
            partService.deletePart(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Part not found: " + e.getMessage());
        }
    }
}
