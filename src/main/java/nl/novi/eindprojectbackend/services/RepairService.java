package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.RepairType;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.repositories.RepairTypeRepository;
import nl.novi.eindprojectbackend.repositories.PartRepository;

import org.springframework.stereotype.Service;

import java.text.ParseException;
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
    private final PartRepository partRepository;
    private final PartService partService;

    public RepairService(CarRepository carRepository,
                         RepairRepository repairRepository,
                         RepairTypeRepository repairTypeRepository,
                         PartRepository partRepository,
                         PartService partService) {
        this.carRepository = carRepository;
        this.repairRepository = repairRepository;
        this.repairTypeRepository = repairTypeRepository;
        this.partRepository = partRepository;
        this.partService = partService;
    }

    public Repair addRepairToCar(Long carId, RepairDto repairDto) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));

        if (repairDto.getRepairTypeId() == null) {
            throw new BadRequestException("Repair type ID is required.");
        }

        RepairType repairType = repairTypeRepository.findById(repairDto.getRepairTypeId())
                .orElseThrow(() -> new RecordNotFoundException("Repair Type", repairDto.getRepairTypeId()));

        Repair repair = new Repair();
        repair.setCar(car);
        repair.setRepairType(repairType);

        repair.setRepairRequestDate(parseDate(repairDto.getRepairRequestDate(), "Repair request date"));
        repair.setRepairDate(parseDate(repairDto.getRepairDate(), "Repair date"));

        double totalCost = repairType.getCost();

        if (repairDto.getPartIds() != null && !repairDto.getPartIds().isEmpty()) {

            List<Part> parts = repairDto.getPartIds().stream()
                    .map(partId -> partService.getPartEntityById(partId))
                    .collect(Collectors.toList());

            for (Part part : parts) {
                if (part.getStock() <= 0) {
                    throw new BadRequestException("Part " + part.getName() + " is out of stock!");
                }

                part.setStock(part.getStock() - 1);
                partRepository.save(part);

                totalCost += part.getPrice();
            }

            repair.setParts(parts);
        }

        repair.setTotalRepairCost(totalCost);

        Repair savedRepair = repairRepository.save(repair);

        car.getRepairs().add(savedRepair);
        car.updateTotalRepairCost();
        carRepository.save(car);

        return savedRepair;
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
            repair.setRepairDate(parseDate(repairDate, "repairDate"));
        }

        if (updates.containsKey("repairRequestDate")) {
            String repairRequestDate = (String) updates.get("repairRequestDate");
            repair.setRepairRequestDate(parseDate(repairRequestDate, "repairRequestDate"));
        }

        if (updates.containsKey("repairTypeId")) {
            Object repairTypeIdObj = updates.get("repairTypeId");
            if (!(repairTypeIdObj instanceof Number)) {
                throw new BadRequestException("Repair type ID must be a number.");
            }
            Long repairTypeId = ((Number) repairTypeIdObj).longValue();
            RepairType repairType = repairTypeRepository.findById(repairTypeId)
                    .orElseThrow(() -> new RecordNotFoundException("Repair Type", repairTypeId));
            repair.setRepairType(repairType);
        }

        if (updates.containsKey("partIds")) {
            List<Long> partIds = (List<Long>) updates.get("partIds");

            List<Part> parts = partIds.stream()
                    .map(partId -> partService.getPartEntityById(partId))
                    .collect(Collectors.toList());

            repair.setParts(parts);
        }

        if (updates.containsKey("totalRepairCost")) {
            Object costObj = updates.get("totalRepairCost");
            if (!(costObj instanceof Number)) {
                throw new BadRequestException("Total repair cost must be a number.");
            }
            repair.setTotalRepairCost(((Number) costObj).doubleValue());
        }

        return repairRepository.save(repair);
    }

    public void addRepair(Repair repair) {
        if (repair.getCar() == null) {
            throw new BadRequestException("Repair must be linked to a car.");
        }

        if (repair.getRepairType() == null) {
            throw new BadRequestException("Repair type is required.");
        }

        repairRepository.save(repair);
    }

    private Date parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " is required.");
        }

        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(dateStr);
        } catch (ParseException e) {
            throw new BadRequestException("Invalid " + fieldName + " format. Use dd-MM-yyyy.");
        }
    }
}
