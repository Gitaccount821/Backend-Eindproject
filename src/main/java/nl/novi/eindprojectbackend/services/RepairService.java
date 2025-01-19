package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepairService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RepairRepository repairRepository;

    public Repair addRepairToCar(Long carId, String repairType, Double cost) {
        // Fetch the car by ID
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        // Create the repair and associate it with the car
        Repair repair = new Repair(repairType, cost, car); // Make sure the constructor matches your Repair entity

        // Add the repair to the car's repairs list
        car.getRepairs().add(repair);

        // Save the repair and the car (note: saving the car will persist both)
        repairRepository.save(repair); // Save the repair
        carRepository.save(car); // Optionally, save the car again to ensure everything is persisted

        return repair; // Return the newly added repair object
    }
}
