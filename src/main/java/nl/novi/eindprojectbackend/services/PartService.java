package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.repositories.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartService {

    @Autowired
    private PartRepository partRepository;

    public Part addPart(Part part) {
        return partRepository.save(part);
    }

    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    public Optional<Part> getPartById(Long id) {
        return partRepository.findById(id);
    }

    public Part updatePart(Part part) {
        if (part.getId() == null || !partRepository.existsById(part.getId())) {
            throw new IllegalArgumentException("Part not found for update.");
        }
        return partRepository.save(part);
    }

    public Part updatePart(Long id, Part partDetails) {
        return partRepository.findById(id).map(part -> {
            part.setStock(partDetails.getStock());
            part.setPrice(partDetails.getPrice());
            return partRepository.save(part);
        }).orElseThrow(() -> new IllegalArgumentException("Part not found"));
    }



    public void deletePart(Long id) {
        if (partRepository.existsById(id)) {
            partRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Part with ID " + id + " not found.");
        }
    }
}
