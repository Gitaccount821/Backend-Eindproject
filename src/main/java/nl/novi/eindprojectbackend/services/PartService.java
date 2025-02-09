package nl.novi.eindprojectbackend.services;

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

    public Part addPart(Part part) {
        return partRepository.save(part);
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Optional<Part> getPartById(Long id) {
        return partRepository.findById(id);
    }

    public void updatePart(Part part) {
        if (part.getId() == null || !partRepository.existsById(part.getId())) {
            throw new IllegalArgumentException("Part not found for update.");
        }
        partRepository.save(part);
    }

    public Part updatePart(Long id, Part partDetails) {
        return partRepository.findById(id).map(part -> {
            part.setStock(partDetails.getStock());
            part.setPrice(partDetails.getPrice());
            return partRepository.save(part);
        }).orElseThrow(() -> new IllegalArgumentException("Part not found"));
    }

    public Part patchPart(Long id, Map<String, Object> updates) {
        Part part = partRepository.findById(id)
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

        return partRepository.save(part);
    }


    public void deletePart(Long id) {
        if (partRepository.existsById(id)) {
            partRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Part with ID " + id + " not found.");
        }
    }
}
