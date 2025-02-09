package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RepairTypeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RepairTypeService {

    private final RepairTypeRepository repairTypeRepository;

    public RepairTypeService(RepairTypeRepository repairTypeRepository) {
        this.repairTypeRepository = repairTypeRepository;
    }

    public RepairType addRepairType(RepairType repairType) {
        if (repairType.getName() == null || repairType.getName().trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }
        if (repairType.getCost() == null || repairType.getCost() <= 0) {
            throw new BadRequestException("Repair type cost must be greater than zero.");
        }
        return repairTypeRepository.save(repairType);
    }

    public List<RepairType> getAllRepairTypes() {
        return repairTypeRepository.findAll();
    }

    public Optional<RepairType> getRepairTypeById(Long id) {
        return Optional.of(repairTypeRepository.findById(id)
                .orElseThrow());
    }

    public RepairType updateRepairType(Long id, RepairType repairType) {
        RepairType existingRepairType = repairTypeRepository.findById(id)
                .orElseThrow();

        if (repairType.getName() == null || repairType.getName().trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }
        if (repairType.getCost() == null || repairType.getCost() <= 0) {
            throw new BadRequestException("Repair type cost must be greater than zero.");
        }

        existingRepairType.setName(repairType.getName());
        existingRepairType.setCost(repairType.getCost());
        existingRepairType.setDescription(repairType.getDescription());

        return repairTypeRepository.save(existingRepairType);
    }

    public RepairType patchRepairType(Long id, Map<String, Object> updates) {
        RepairType repairType = repairTypeRepository.findById(id)
                .orElseThrow();

        if (updates.containsKey("name")) {
            String name = (String) updates.get("name");
            if (name == null || name.trim().isEmpty()) {
                throw new BadRequestException("Repair type name cannot be empty.");
            }
            repairType.setName(name);
        }

        if (updates.containsKey("cost")) {
            Double cost = (Double) updates.get("cost");
            if (cost == null || cost <= 0) {
                throw new BadRequestException("Repair type cost must be greater than zero.");
            }
            repairType.setCost(cost);
        }

        if (updates.containsKey("description")) {
            repairType.setDescription((String) updates.get("description"));
        }

        return repairTypeRepository.save(repairType);
    }

    public void deleteRepairType(Long id) {
        if (!repairTypeRepository.existsById(id)) {
            throw new RepairTypeNotFoundException(String.valueOf(id));
        }
        repairTypeRepository.deleteById(id);
    }
}
