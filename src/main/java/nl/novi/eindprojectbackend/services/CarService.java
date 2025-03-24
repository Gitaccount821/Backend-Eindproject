package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.ForbiddenActionException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.mappers.CarMapper;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.User;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
import nl.novi.eindprojectbackend.repositories.RepairRepository;
import nl.novi.eindprojectbackend.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final PdfAttachmentRepository pdfAttachmentRepository;
    private final RepairRepository repairRepository;
    private final UserRepository userRepository;

    public CarService(CarRepository carRepository,
                      PdfAttachmentRepository pdfAttachmentRepository,
                      RepairRepository repairRepository,
                      UserRepository userRepository) {
        this.carRepository = carRepository;
        this.pdfAttachmentRepository = pdfAttachmentRepository;
        this.repairRepository = repairRepository;
        this.userRepository = userRepository;
    }


    public CarDto addCar(CarDto carDto) {
        User owner = userRepository.findById(carDto.getOwnerUsername())
                .orElseThrow(() -> new RecordNotFoundException("User", carDto.getOwnerUsername()));

        Car car = CarMapper.toEntity(carDto, owner);

        Car savedCar = carRepository.save(car);

        return CarMapper.toDto(savedCar);
    }

    public List<CarDto> getAllCars() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isKlant = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_KLANT"));

        if (isKlant) {
            return carRepository.findAll().stream()
                    .filter(car -> car.getOwner().getUsername().equals(currentUsername))
                    .map(CarMapper::toDto)
                    .collect(Collectors.toList());
        }

        return carRepository.findAll()
                .stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());
    }



    public CarDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isKlant = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_KLANT"));

        if (isKlant && !car.getOwner().getUsername().equals(currentUsername)) {
            throw new ForbiddenActionException();
        }

        return CarMapper.toDto(car);
    }



    public CarDto updateCar(Long id, CarDto carDto) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

        User owner = userRepository.findById(carDto.getOwnerUsername())
                .orElseThrow(() -> new RecordNotFoundException("User", carDto.getOwnerUsername()));

        existingCar.setCarType(carDto.getCarType());
        existingCar.setRepairRequestDate(carDto.getRepairRequestDate());
        existingCar.setOwner(owner);

        existingCar.updateTotalRepairCost();

        Car savedCar = carRepository.save(existingCar);

        return CarMapper.toDto(savedCar);
    }


    public CarDto patchCar(Long id, Map<String, Object> updates) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Car", id));

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
            } catch (ParseException e) {
                throw new BadRequestException("Invalid repair request date format. Use dd-MM-yyyy.");
            }
        }

        if (updates.containsKey("ownerUsername")) {
            String ownerUsername = (String) updates.get("ownerUsername");

            User owner = userRepository.findById(ownerUsername)
                    .orElseThrow(() -> new RecordNotFoundException("User", ownerUsername));

            car.setOwner(owner);
        }


        Car savedCar = carRepository.save(car);

        return CarMapper.toDto(savedCar);
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
}
