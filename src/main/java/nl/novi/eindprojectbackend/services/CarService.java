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
    private RepairRepository RepairRepository;

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
            car.setRepairDate(updatedCar.getRepairDate());  // This will work once `repairDate` is back in the Car model
            car.setRepairs(updatedCar.getRepairs());
            car.setTotalRepairCost(updatedCar.getTotalRepairCost());
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


    public Car addRepairToCar(Long carId, Repair repair) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));


        if (car.getRepairs() == null) {
            car.setRepairs(List.of(repair));
        } else {
            car.getRepairs().add(repair);
        }


        double totalCost = car.getRepairs().stream()
                .mapToDouble(Repair::getCost)
                .sum();
        car.setTotalRepairCost(totalCost);

        return carRepository.save(car);
    }

    public List<Repair> getRepairsByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        return car.getRepairs();
    }

    public nl.novi.eindprojectbackend.repositories.RepairRepository getRepairRepository() {
        return RepairRepository;
    }

    public void setRepairRepository(nl.novi.eindprojectbackend.repositories.RepairRepository repairRepository) {
        RepairRepository = repairRepository;
    }
}
