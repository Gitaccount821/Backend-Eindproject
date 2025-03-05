package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.PartService;
import nl.novi.eindprojectbackend.exceptions.*;

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

    private void validatePart(String name, Object priceObj, Object stockObj) {
        if (name != null && name.trim().isEmpty()) {
            throw new BadRequestException("Part name", true);
        }

        if (priceObj != null) {
            if (!(priceObj instanceof Number)) {
                throw new BadRequestException("Price must be a number.");
            }

            double price = ((Number) priceObj).doubleValue();
            if (price < 0.0) {
                throw new BadRequestException("Price must be a positive value.");
            }
        }

        if (stockObj != null) {
            if (!(stockObj instanceof Number)) {
                throw new BadRequestException("Stock must be a number.");
            }

            int stock = ((Number) stockObj).intValue();
            if (stock < 0) {
                throw new BadRequestException("Stock must be a non-negative value.");
            }
        }
    }

    @PostMapping
    public ResponseEntity<?> addPart(@Valid @RequestBody Part part) {
        try {
            validatePart(part.getName(), part.getPrice(), part.getStock());

            Part savedPart = partService.addPart(part);
            return ResponseEntity.ok(savedPart);

        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Error adding part.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Part> getPartById(@PathVariable Long id) {
        return ResponseEntity.ok(
                partService.getPartById(id)
                        .orElseThrow(() -> new RecordNotFoundException("Part", id))
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        try {

            partService.getPartById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Part", id));

            validatePart(partDetails.getName(), partDetails.getPrice(), partDetails.getStock());

            Part updatedPart = partService.updatePart(id, partDetails);
            return ResponseEntity.ok(updatedPart);

        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Part", id);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Error updating part.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchPart(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {

            partService.getPartById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Part", id));

            validatePart(
                    updates.get("name") != null ? (String) updates.get("name") : null,
                    updates.get("price"),
                    updates.get("stock")
            );

            Part updatedPart = partService.patchPart(id, updates);
            return ResponseEntity.ok(updatedPart);

        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Part", id);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Error updating part.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePart(@PathVariable Long id) {
        partService.getPartById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        partService.deletePart(id);
        return ResponseEntity.ok("Part deleted successfully.");
    }
}
