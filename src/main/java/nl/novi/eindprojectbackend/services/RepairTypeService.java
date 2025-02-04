package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RepairTypeService {

    @Autowired
    private RepairTypeRepository repairTypeRepository;

    public RepairType addRepairType(RepairType repairType) {
        return repairTypeRepository.save(repairType);
    }

    public List<RepairType> getAllRepairTypes() {
        return repairTypeRepository.findAll();
    }

    public Optional<RepairType> getRepairTypeById(Long id) {
        return repairTypeRepository.findById(id);
    }



    public RepairType updateRepairType(Long id, RepairType repairType) {
        return repairTypeRepository.findById(id)
                .map(existingType -> {
                    existingType.setName(repairType.getName());
                    existingType.setCost(repairType.getCost());
                    existingType.setDescription(repairType.getDescription());
                    return repairTypeRepository.save(existingType);
                })
                .orElseThrow(() -> new IllegalArgumentException("Repair type not found"));
    }

    public RepairType patchRepairType(Long id, Map<String, Object> updates) {
        RepairType repairType = repairTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Repair Type not found"));

        if (updates.containsKey("name")) {
            repairType.setName((String) updates.get("name"));
        }
        if (updates.containsKey("cost")) {
            repairType.setCost(((Number) updates.get("cost")).doubleValue());
        }
        if (updates.containsKey("description")) {
            repairType.setDescription((String) updates.get("description"));
        }

        return repairTypeRepository.save(repairType);
    }



    public void deleteRepairType(Long id) {
        repairTypeRepository.deleteById(id);
    }
}
