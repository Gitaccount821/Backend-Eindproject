package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RepairService {

    private final CarRepository carRepository;
    private final RepairRepository repairRepository;
    private final RepairTypeRepository repairTypeRepository;
    private final PartService partService;

    public RepairService(CarRepository carRepository, RepairRepository repairRepository, RepairTypeRepository repairTypeRepository, PartService partService) {
        this.carRepository = carRepository;
        this.repairRepository = repairRepository;
        this.repairTypeRepository = repairTypeRepository;
        this.partService = partService;
    }

    public Repair patchRepair(Long carId, Long repairId, Map<String, Object> updates) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));

        Repair repair = car.getRepairs().stream()
                .filter(r -> r.getId().equals(repairId))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Repair", repairId));

        if (updates.containsKey("repairDate")) {
            String repairDate = (String) updates.get("repairDate");
            if (repairDate.isEmpty()) {
                throw new BadRequestException("Repair date", true);
            }
            repair.setRepairDate(parseDate(repairDate, "Repair date"));
        }

        if (updates.containsKey("repairRequestDate")) {
            String repairRequestDate = (String) updates.get("repairRequestDate");
            if (repairRequestDate.isEmpty()) {
                throw new BadRequestException("Repair request date", true);
            }
            repair.setRepairRequestDate(parseDate(repairRequestDate, "Repair request date"));
        }

        if (updates.containsKey("repairTypeId")) {
            Object repairTypeIdObj = updates.get("repairTypeId");
            if (!(repairTypeIdObj instanceof Number)) {
                throw new BadRequestException("Repair type ID must be a valid number.");
            }
            RepairType repairType = repairTypeRepository.findById(((Number) repairTypeIdObj).longValue())
                    .orElseThrow(() -> new RecordNotFoundException("Repair Type", ((Number) repairTypeIdObj).longValue()));
            repair.setRepairType(repairType);
        }

        if (updates.containsKey("partIds")) {
            List<Long> partIds = (List<Long>) updates.get("partIds");
            List<Part> parts = partIds.stream()
                    .map(partId -> partService.getPartById(partId)
                            .orElseThrow(() -> new RecordNotFoundException("Part", partId)))
                    .collect(Collectors.toList());
            repair.setParts(parts);
        }

        if (updates.containsKey("totalRepairCost")) {
            Object costObj = updates.get("totalRepairCost");
            if (!(costObj instanceof Number)) {
                throw new BadRequestException("Total repair cost must be a valid number.");
            }
            repair.setTotalRepairCost(((Number) costObj).doubleValue());
        }

        return repairRepository.save(repair);
    }

    private Date parseDate(String dateStr, String fieldName) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
        } catch (Exception e) {
            throw new BadRequestException("Invalid " + fieldName + " format. Use dd-MM-yyyy.");
        }
    }

    public void addRepair(Repair repair) {
        if (repair.getCar() == null) {
            throw new BadRequestException("Repair must be associated with a car.");
        }
        repairRepository.save(repair);
    }
}
