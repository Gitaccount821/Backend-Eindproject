package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PdfAttachmentRepository pdfAttachmentRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private UserRepository userRepository;

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





    public PdfAttachment addPdfAttachment(Long carId, PdfAttachment attachment) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        attachment.setCar(car);
        return pdfAttachmentRepository.save(attachment);
    }

    public PdfAttachment getAttachmentByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        if (car.getPdfAttachment() == null) {
            throw new IllegalArgumentException("No attachment found for this car");
        }

        return car.getPdfAttachment();
    }

    public Car addRepairToCar(Long carId, Repair repair) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        repair.setCar(car);
        car.getRepairs().add(repair);

        car.updateTotalRepairCost();

        carRepository.save(car);
        return car;
    }

    public List<Repair> getRepairsByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        return car.getRepairs();
    }
}
