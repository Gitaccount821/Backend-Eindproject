package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.repositories.PartRepository;
import nl.novi.eindprojectbackend.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part addPart(Part part) {
        validatePart(part);
        return partRepository.save(part);
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Optional<Part> getPartById(Long id) {
        return Optional.of(partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id)));
    }

    public Part updatePart(Long id, Part partDetails) {
        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        validatePart(partDetails);

        existingPart.setStock(partDetails.getStock());
        existingPart.setPrice(partDetails.getPrice());
        existingPart.setName(partDetails.getName());

        return partRepository.save(existingPart);
    }

    public Part patchPart(Long id, Map<String, Object> updates) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        validatePartPatch(updates);

        if (updates.containsKey("price")) {
            part.setPrice(((Number) updates.get("price")).doubleValue());
        }

        if (updates.containsKey("stock")) {
            part.setStock(((Number) updates.get("stock")).intValue());
        }

        if (updates.containsKey("name")) {
            part.setName((String) updates.get("name"));
        }

        return partRepository.save(part);
    }

    public void deletePart(Long id) {
        if (!partRepository.existsById(id)) {
            throw new RecordNotFoundException("Part", id);
        }
        partRepository.deleteById(id);
    }

    private void validatePart(Part part) {
        if (part == null) {
            throw new BadRequestException("Part cannot be null.");
        }

        ValidationUtils.validateNotEmpty(part.getName(), "Part name");
        ValidationUtils.validatePositiveNumber(part.getPrice(), "Part price");
        ValidationUtils.validateNonNegativeNumber(part.getStock(), "Part stock");
    }

    private void validatePartPatch(Map<String, Object> updates) {
        if (updates == null || updates.isEmpty()) {
            throw new BadRequestException("No data provided for update.");
        }

        if (updates.containsKey("name")) {
            Object nameObj = updates.get("name");
            if (!(nameObj instanceof String)) {
                throw new BadRequestException("Part name must be a string.");
            }
            ValidationUtils.validateNotEmpty((String) nameObj, "Part name");
        }

        if (updates.containsKey("price")) {
            ValidationUtils.validatePositiveNumber(updates.get("price"), "Part price");
        }

        if (updates.containsKey("stock")) {
            ValidationUtils.validateNonNegativeNumber(updates.get("stock"), "Part stock");
        }
    }
}
