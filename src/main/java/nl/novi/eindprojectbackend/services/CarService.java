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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final PdfAttachmentRepository pdfAttachmentRepository;
    private final RepairRepository repairRepository;
    private final UserRepository userRepository;

    public CarService(CarRepository carRepository, PdfAttachmentRepository pdfAttachmentRepository, RepairRepository repairRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.pdfAttachmentRepository = pdfAttachmentRepository;
        this.repairRepository = repairRepository;
        this.userRepository = userRepository;
    }

    public Car addCar(Car car, String ownerUsername) {
        User owner = userRepository.findById(ownerUsername)
                .orElseThrow(() -> new BadRequestException("Invalid ownerUsername: No such user exists."));
        car.setOwner(owner);
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return Optional.ofNullable(carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id)));
    }

    public Car updateCar(Long id, Car updatedCar) {
        return carRepository.findById(id).map(car -> {
            car.setCarType(updatedCar.getCarType());
            car.setOwner(updatedCar.getOwner());
            car.setRepairs(updatedCar.getRepairs());
            car.updateTotalRepairCost();
            return carRepository.save(car);
        }).orElseThrow(() -> new RecordNotFoundException("Car", id));
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        List<Repair> repairs = car.getRepairs();
        repairRepository.deleteAll(repairs);

        if (car.getPdfAttachment() != null) {
            pdfAttachmentRepository.delete(car.getPdfAttachment());
        }

        carRepository.delete(car);
    }

    // deze weghalen brak eerder de car patching, dus ik laat het nu express staan.
    public Car updateCar(Long id, CarDto carDto) {
        if (carDto.getCarType() == null || carDto.getCarType().isEmpty()) {
            throw new BadRequestException("Car type is required.");
        }

        if (carDto.getRepairRequestDate() == null || carDto.getRepairRequestDate().isEmpty()) {
            throw new BadRequestException("Repair request date is required.");
        }

        return carRepository.findById(id).map(car -> {
            car.setCarType(carDto.getCarType());
            car.setRepairRequestDate(carDto.getRepairRequestDate());
            return carRepository.save(car);
        }).orElseThrow(() -> new RecordNotFoundException("Car", id));
    }

    public Car patchCar(Long id, Map<String, Object> updates) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        if (updates.containsKey("carType")) {
            car.setCarType((String) updates.get("carType"));
        }

        if (updates.containsKey("repairRequestDate")) {
            car.setRepairRequestDate((String) updates.get("repairRequestDate"));
        }

        return carRepository.save(car);
    }
}
