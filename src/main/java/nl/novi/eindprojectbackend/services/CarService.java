package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final PdfAttachmentRepository pdfAttachmentRepository;
    private final RepairRepository repairRepository;
    private final UserRepository userRepository;

    public CarService(CarRepository carRepository, PdfAttachmentRepository pdfAttachmentRepository,
                      RepairRepository repairRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.pdfAttachmentRepository = pdfAttachmentRepository;
        this.repairRepository = repairRepository;
        this.userRepository = userRepository;
    }

    public Car addCar(Car car, String ownerUsername) {
        if (car.getCarType() == null || car.getCarType().trim().isEmpty()) {
            throw new BadRequestException("Car type is required.");
        }

        if (car.getRepairRequestDate() == null || car.getRepairRequestDate().trim().isEmpty()) {
            throw new BadRequestException("Repair request date is required.");
        }

        // Validate owner
        User owner = userRepository.findById(ownerUsername)
                .orElseThrow(() -> new RecordNotFoundException("User", null));
        car.setOwner(owner);

        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));
    }

    public Car updateCar(Long id, Car updatedCar) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        if (updatedCar.getCarType() == null || updatedCar.getCarType().trim().isEmpty()) {
            throw new BadRequestException("Car type is required.");
        }

        if (updatedCar.getRepairRequestDate() == null || updatedCar.getRepairRequestDate().trim().isEmpty()) {
            throw new BadRequestException("Repair request date is required.");
        }

        existingCar.setCarType(updatedCar.getCarType());
        existingCar.setOwner(updatedCar.getOwner());
        existingCar.setRepairRequestDate(updatedCar.getRepairRequestDate());

        existingCar.updateTotalRepairCost(); // You have this in your model
        return carRepository.save(existingCar);
    }

    public Car patchCar(Long id, Map<String, Object> updates) {
        Car car = getCarById(id);

        if (updates.containsKey("carType")) {
            String carType = (String) updates.get("carType");
            if (carType == null || carType.trim().isEmpty()) {
                throw new BadRequestException("Car type is required.");
            }
            car.setCarType(carType);
        }

        if (updates.containsKey("repairRequestDate")) {
            String repairRequestDate = (String) updates.get("repairRequestDate");
            if (repairRequestDate == null || repairRequestDate.trim().isEmpty()) {
                throw new BadRequestException("Repair request date is required.");
            }

            try {
                new SimpleDateFormat("dd-MM-yyyy").parse(repairRequestDate);
                car.setRepairRequestDate(repairRequestDate);
            } catch (Exception e) {
                throw new BadRequestException("Invalid repair request date format. Use dd-MM-yyyy.");
            }
        }

        if (updates.containsKey("ownerUsername")) {
            String ownerUsername = (String) updates.get("ownerUsername");
            User owner = userRepository.findById(ownerUsername)
                    .orElseThrow(() -> new RecordNotFoundException("User", null));
            car.setOwner(owner);
        }

        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        Car car = getCarById(id);

        List<Repair> repairs = car.getRepairs();
        repairRepository.deleteAll(repairs);

        if (car.getPdfAttachment() != null) {
            pdfAttachmentRepository.delete(car.getPdfAttachment());
        }

        carRepository.delete(car);
    }
}
