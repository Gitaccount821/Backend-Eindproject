package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;




@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PdfAttachmentRepository pdfAttachmentRepository;

    @Autowired
    private RepairRepository repairRepository;

    // Nieuwe auto toevoegen
    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    // Get alle autos
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // Get auto bij ID
    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    // Update auto details
    public Car updateCar(Long id, Car updatedCar) {
        return carRepository.findById(id).map(car -> {

            car.setCarType(updatedCar.getCarType());
            car.setClientNumber(updatedCar.getClientNumber());

            car.setRepairs(updatedCar.getRepairs());

            double totalCost = updatedCar.getRepairs().stream()
                    .mapToDouble(repair -> repair.getCost() != null ? repair.getCost() : 0.0) // Safeguard against null costs
                    .sum();
            car.setTotalRepairCost(totalCost);

            return carRepository.save(car);
        }).orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }

    // Delete auto via ID
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    // Add PDF attachment
    public PdfAttachment addPdfAttachment(Long carId, PdfAttachment attachment) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        attachment.setCar(car);
        return pdfAttachmentRepository.save(attachment);
    }

    public List<PdfAttachment> getAttachmentsByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        return car.getAttachments();
    }

    // Add repair to car
    public Car addRepairToCar(Long carId, Repair repair) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        repair.setCar(car);

        car.getRepairs().add(repair);

        double totalCost = car.getRepairs().stream()
                .mapToDouble(r -> r.getCost() != null ? r.getCost() : 0.0)
                .sum();

        car.setTotalRepairCost(totalCost);

        carRepository.save(car);
        return car;
    }

    public List<Repair> getRepairsByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        return car.getRepairs();
    }

    // Getters and Setters
    public RepairRepository getRepairRepository() {
        return repairRepository;
    }

    public void setRepairRepository(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }
}
