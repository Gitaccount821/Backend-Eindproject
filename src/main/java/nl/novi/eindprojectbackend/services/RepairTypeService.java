package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RepairTypeService {

    private final RepairTypeRepository repairTypeRepository;

    public RepairTypeService(RepairTypeRepository repairTypeRepository) {
        this.repairTypeRepository = repairTypeRepository;
    }

    public RepairType addRepairType(RepairType repairType) {
        validateRepairType(repairType);
        return repairTypeRepository.save(repairType);
    }

    public List<RepairType> getAllRepairTypes() {
        return repairTypeRepository.findAll();
    }

    public RepairType getRepairTypeById(Long id) {
        return repairTypeRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", id));
    }

    public RepairType updateRepairType(Long id, RepairType repairType) {
        RepairType existingRepairType = getRepairTypeById(id);

        validateRepairType(repairType);

        existingRepairType.setName(repairType.getName());
        existingRepairType.setCost(repairType.getCost());
        existingRepairType.setDescription(repairType.getDescription());

        return repairTypeRepository.save(existingRepairType);
    }

    public RepairType patchRepairType(Long id, Map<String, Object> updates) {
        RepairType repairType = getRepairTypeById(id);

        String name = updates.containsKey("name") ? (String) updates.get("name") : repairType.getName();
        Double cost = repairType.getCost();
        String description = updates.containsKey("description") ? (String) updates.get("description") : repairType.getDescription();

        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }

        Object costObj = updates.get("cost");
        if (costObj != null) {
            if (costObj instanceof Integer) {
                cost = ((Integer) costObj).doubleValue();
            } else if (costObj instanceof Double) {
                cost = (Double) costObj;
            } else {
                throw new BadRequestException("Repair type cost must be a valid number.");
            }

            if (cost <= 0.0) {
                throw new BadRequestException("Repair type cost must be greater than zero.");
            }
        }

        if (description == null || description.trim().isEmpty()) {
            throw new BadRequestException("Repair type description cannot be empty.");
        }

        repairType.setName(name);
        repairType.setCost(cost);
        repairType.setDescription(description);

        return repairTypeRepository.save(repairType);
    }

    public void deleteRepairType(Long id) {
        RepairType repairType = getRepairTypeById(id);
        repairTypeRepository.delete(repairType);
    }

    private void validateRepairType(RepairType repairType) {
        if (repairType.getName() == null || repairType.getName().trim().isEmpty()) {
            throw new BadRequestException("Repair type name cannot be empty.");
        }

        if (repairType.getCost() == null || repairType.getCost() <= 0.0) {
            throw new BadRequestException("Repair type cost must be greater than zero.");
        }

        if (repairType.getDescription() == null || repairType.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Repair type description cannot be empty.");
        }
    }
}
