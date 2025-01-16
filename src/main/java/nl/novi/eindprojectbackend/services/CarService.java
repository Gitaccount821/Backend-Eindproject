package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.models.Repair; // Import the Repair enum
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
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

    // nieuwe auto toevoegen
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
            car.setRepairDate(updatedCar.getRepairDate());
            car.setRepairs(updatedCar.getRepairs());
            car.setTotalRepairCost(updatedCar.getTotalRepairCost());
            return carRepository.save(car);
        }).orElseThrow(() -> new IllegalArgumentException("Car not found"));
    }

    // Delete auto via ID
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    // Add PDF attachment (niet zeker of dit gaat werken)
    public PdfAttachment addPdfAttachment(Long carId, PdfAttachment attachment) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        attachment.setCar(car);
        return pdfAttachmentRepository.save(attachment);
    }

    // Get alle attachments
    public List<PdfAttachment> getAttachmentsByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        return car.getAttachments();
    }

    // Een enkele repair toevoegen aan een auto
    public Car addRepairToCar(Long carId, Repair repair) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        if (car.getRepairs() == null) {
            car.setRepairs(List.of(repair)); // Als er geen reparaties zijn, start met een nieuwe lijst
        } else {
            car.getRepairs().add(repair); // Voeg de nieuwe reparatie toe aan de lijst
        }

        return carRepository.save(car); // Sla de wijzigingen op
    }

    // Alle repairs van een specifieke auto ophalen
    public List<Repair> getRepairsByCarId(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        return car.getRepairs(); // Retourneer de lijst van reparaties
    }
}
