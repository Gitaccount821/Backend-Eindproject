package nl.novi.eindprojectbackend.services;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
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
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        Repair repair = car.getRepairs().stream()
                .filter(r -> r.getId().equals(repairId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Repair not found for this car"));

        if (updates.containsKey("repairDate")) {
            String repairDate = (String) updates.get("repairDate");
            if (repairDate.isEmpty()) {
                throw new BadRequestException("Repair date cannot be empty.");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date parsedDate = sdf.parse(repairDate);
                repair.setRepairDate(parsedDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Use dd-MM-yyyy.");
            }
        }

        if (updates.containsKey("repairRequestDate")) {
            String repairRequestDate = (String) updates.get("repairRequestDate");
            if (repairRequestDate.isEmpty()) {
                throw new BadRequestException("Repair request date cannot be empty.");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date parsedDate = sdf.parse(repairRequestDate);
                repair.setRepairRequestDate(parsedDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Use dd-MM-yyyy.");
            }
        }

        if (updates.containsKey("repairTypeId")) {
            Object repairTypeIdObj = updates.get("repairTypeId");
            if (repairTypeIdObj == null || !(repairTypeIdObj instanceof Number)) {
                throw new IllegalArgumentException("Repair type ID is required and must be a valid number.");
            }
            RepairType repairType = repairTypeRepository.findById(((Number) repairTypeIdObj).longValue())
                    .orElseThrow(() -> new IllegalArgumentException("Repair Type not found"));
            repair.setRepairType(repairType);
        }

        if (updates.containsKey("partIds")) {
            List<Long> partIds = (List<Long>) updates.get("partIds");
            List<Part> parts = partIds.stream()
                    .map(partId -> partService.getPartById(partId)
                            .orElseThrow(() -> new IllegalArgumentException("Part not found for ID: " + partId)))
                    .collect(Collectors.toList());

            repair.setParts(parts);
        }

        if (updates.containsKey("totalRepairCost")) {
            repair.setTotalRepairCost(((Number) updates.get("totalRepairCost")).doubleValue());
        }

        return repairRepository.save(repair);
    }



    public void addRepair(Repair repair) {
        if (repair.getCar() == null) {
            throw new IllegalArgumentException("Repair must be associated with a car.");
        }
        repairRepository.save(repair);
    }

}
