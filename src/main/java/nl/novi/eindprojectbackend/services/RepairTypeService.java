package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
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

    private void validateRepairType(String name, Double cost, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }
        if (cost == null || cost <= 0) {
            throw new BadRequestException("Repair type cost must be greater than zero.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new BadRequestException("Repair type description cannot be empty.");
        }
    }

    public RepairType addRepairType(RepairType repairType) {
        validateRepairType(repairType.getName(), repairType.getCost(), repairType.getDescription());
        return repairTypeRepository.save(repairType);
    }

    public List<RepairType> getAllRepairTypes() {
        return repairTypeRepository.findAll();
    }

    public Optional<RepairType> getRepairTypeById(Long id) {
        return Optional.of(repairTypeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id)));
    }

    public RepairType updateRepairType(Long id, RepairType repairType) {
        RepairType existingRepairType = repairTypeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));

        validateRepairType(repairType.getName(), repairType.getCost(), repairType.getDescription());

        existingRepairType.setName(repairType.getName());
        existingRepairType.setCost(repairType.getCost());
        existingRepairType.setDescription(repairType.getDescription());

        return repairTypeRepository.save(existingRepairType);
    }

    public RepairType patchRepairType(Long id, Map<String, Object> updates) {
        RepairType repairType = repairTypeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));

        String name = updates.containsKey("name") ? (String) updates.get("name") : repairType.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }

        Object costObj = updates.get("cost");
        Double cost = repairType.getCost();

        if (costObj instanceof Integer) {
            cost = ((Integer) costObj).doubleValue();
        } else if (costObj instanceof Double) {
            cost = (Double) costObj;
        } else if (costObj != null) {
            throw new BadRequestException("Repair type cost must be a valid number.");
        }

        if (cost <= 0.0) {
            throw new BadRequestException("Repair type cost must be greater than zero.");
        }

        String description = updates.containsKey("description") ? (String) updates.get("description") : repairType.getDescription();
        if (description == null || description.trim().isEmpty()) {
            throw new BadRequestException("Repair type description cannot be empty.");
        }

        repairType.setName(name);
        repairType.setCost(cost);
        repairType.setDescription(description);

        return repairTypeRepository.save(repairType);
    }

    public void deleteRepairType(Long id) {
        if (!repairTypeRepository.existsById(id)) {
            throw new RecordNotFoundException("Repair Type", id);
        }
        repairTypeRepository.deleteById(id);
    }
}