package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.CarDto;
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
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + ownerUsername));
        car.setOwner(owner);

        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Car updateCar(Long id, Car updatedCar) {
        return carRepository.findById(id).map(car -> {
            car.setCarType(updatedCar.getCarType());
            car.setOwner(updatedCar.getOwner());
            car.setRepairs(updatedCar.getRepairs());

            car.updateTotalRepairCost();

            return carRepository.save(car);
        }).orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }

    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));


        List<Repair> repairs = car.getRepairs();
        repairRepository.deleteAll(repairs);


        if (car.getPdfAttachment() != null) {
            pdfAttachmentRepository.delete(car.getPdfAttachment());
        }


        carRepository.delete(car);
        System.out.println("Car deleted successfully: " + id);
    }

    public Car updateCar(Long id, CarDto carDto) {
        return carRepository.findById(id).map(car -> {
            car.setCarType(carDto.getCarType());
            car.setRepairRequestDate(carDto.getRepairRequestDate());
            return carRepository.save(car);
        }).orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }

    public Car patchCar(Long id, Map<String, Object> updates) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        if (updates.containsKey("carType")) {
            car.setCarType((String) updates.get("carType"));
        }
        if (updates.containsKey("repairRequestDate")) {
            car.setRepairRequestDate((String) updates.get("repairRequestDate"));
        }

        return carRepository.save(car);
    }






}
