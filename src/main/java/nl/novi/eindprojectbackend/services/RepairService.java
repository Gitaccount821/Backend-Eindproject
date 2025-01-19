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

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));


        Repair repair = new Repair(repairType, cost, car);


        car.getRepairs().add(repair);


        repairRepository.save(repair);
        carRepository.save(car);

        return repair;
    }
}
