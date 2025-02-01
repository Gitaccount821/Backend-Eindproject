package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<RepairType> updateRepairType(Long id, RepairType repairType) {
        return repairTypeRepository.findById(id).map(existingType -> {
            existingType.setName(repairType.getName());
            existingType.setCost(repairType.getCost());
            existingType.setDescription(repairType.getDescription());
            return repairTypeRepository.save(existingType);
        });
    }

    public void deleteRepairType(Long id) {
        repairTypeRepository.deleteById(id);
    }
}
