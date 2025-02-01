package nl.novi.eindprojectbackend.services;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RepairService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RepairRepository repairRepository;


    public Repair addRepair(Repair repair) {
        if (repair.getCar() == null) {
            throw new IllegalArgumentException("Repair must be associated with a car.");
        }
        return repairRepository.save(repair);
    }

}
