package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.repositories.PartRepository;
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

    private void validatePart(Part part) {
        if (part.getPrice() == null) {
            throw new BadRequestException("Price cannot be null.");
        }
        if (part.getPrice() < 0) {
            throw new BadRequestException("Price must be a positive value.");
        }
        if (part.getStock() == null) {
            throw new BadRequestException("Stock cannot be null.");
        }
        if (part.getStock() < 0) {
            throw new BadRequestException("Stock must be a non-negative value.");
        }
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

    public void updatePart(Part part) {
        validatePart(part);
        partRepository.save(part);
    }

    public Part updatePart(Long id, Part partDetails) {
        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        validatePart(partDetails);

        existingPart.setStock(partDetails.getStock());
        existingPart.setPrice(partDetails.getPrice());

        return partRepository.save(existingPart);
    }

    public Part patchPart(Long id, Map<String, Object> updates) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Part", id));

        if (updates.containsKey("price")) {
            Object priceObj = updates.get("price");
            if (!(priceObj instanceof Number)) {
                throw new BadRequestException("Price must be a valid number.");
            }
            double price = ((Number) priceObj).doubleValue();
            if (price < 0) {
                throw new BadRequestException("Price must be a positive value.");
            }
            part.setPrice(price);
        }

        if (updates.containsKey("stock")) {
            Object stockObj = updates.get("stock");
            if (!(stockObj instanceof Number)) {
                throw new BadRequestException("Stock must be a valid number.");
            }
            int stock = ((Number) stockObj).intValue();
            if (stock < 0) {
                throw new BadRequestException("Stock must be a non-negative value.");
            }
            part.setStock(stock);
        }

        if (updates.containsKey("name")) {
            String name = (String) updates.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new BadRequestException("Part name cannot be empty.");
            }
            part.setName(name);
        }

        return partRepository.save(part);
    }

    public void deletePart(Long id) {
        if (!partRepository.existsById(id)) {
            throw new RecordNotFoundException("Part", id);
        }
        partRepository.deleteById(id);
    }
}
