package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.services.CarService;
import nl.novi.eindprojectbackend.services.RepairService;
import nl.novi.eindprojectbackend.services.PartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/cars", produces = "application/json")
public class CarController {

    @Autowired
    private CarService carService;

    @Autowired
    private RepairService repairService;

    @Autowired
    private PartService partService; // Added service to handle parts


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<CarDto> addCar(@RequestBody CarDto carDto) {
        if (carDto.getCarType() == null || carDto.getClientNumber() == null) {
            return ResponseEntity.badRequest().build();  // Return 400 if missing fields
        }

        Car car = new Car();
        car.setCarType(carDto.getCarType());
        car.setClientNumber(carDto.getClientNumber());

        // Ensure not null
        if (carDto.getRepairRequestDate() != null) {
            car.setRepairRequestDate(carDto.getRepairRequestDate());
        }

        Car savedCar = carService.addCar(car);

        CarDto responseDto = new CarDto(savedCar);
        return ResponseEntity.ok(responseDto);
    }


    @GetMapping(produces = "application/json")
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        List<CarDto> carDtos = cars.stream()
                .map(this::convertToCarDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(carDtos);
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        return carService.getCarById(id)
                .map(car -> ResponseEntity.ok(convertToCarDto(car)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<CarDto> updateCar(@PathVariable Long id, @RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCar(id, car);
            CarDto carDto = convertToCarDto(updatedCar);
            return ResponseEntity.ok(carDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{carId}/repairs", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CarDto> addRepairToCar(@PathVariable Long carId, @RequestBody RepairDto repairDto) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date repairRequestDate = sdf.parse(repairDto.getRepairRequestDate());

            Car car = carService.getCarById(carId).orElseThrow(() -> new IllegalArgumentException("Car not found"));

            Repair repair = new Repair();
            repair.setRepairType(repairDto.getRepairType());
            repair.setCost(repairDto.getCost());
            repair.setRepairRequestDate(repairRequestDate);
            repair.setRepairDate(null);
            repair.setCar(car);

            if (repairDto.getPartIds() != null && !repairDto.getPartIds().isEmpty()) {
                List<Part> parts = repairDto.getPartIds().stream()
                        .map(partId -> partService.getPartById(partId)
                                .orElseThrow(() -> new IllegalArgumentException("Part not found for ID: " + partId)))
                        .collect(Collectors.toList());

                repair.setParts(parts);

                // total cost
                double totalCost = repair.getCost();
                for (Part part : parts) {
                    totalCost += part.getPrice();
                    part.setStock(part.getStock() - 1); // Decrease stock by 1
                    partService.updatePart(part.getId(), part); // Save the updated part stock
                }
                repair.setTotalRepairCost(totalCost);
            }

            // Save the repair
            carService.addRepairToCar(carId, repair);

            // Return updated car info
            Car updatedCar = carService.getCarById(carId).orElseThrow(() -> new IllegalArgumentException("Car not found"));
            CarDto carDto = convertToCarDto(updatedCar);
            return ResponseEntity.ok(carDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Add PDF attachment
    @PostMapping(value = "/{id}/attachments", produces = "application/json", consumes = "application/json")
    public ResponseEntity<AttachmentDto> addPdfAttachment(
            @PathVariable Long id,
            @RequestBody PdfAttachment attachment) {
        try {
            PdfAttachment savedAttachment = carService.addPdfAttachment(id, attachment);
            AttachmentDto attachmentDto = convertToAttachmentDto(savedAttachment);
            return ResponseEntity.ok(attachmentDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value = "/{id}/attachments", produces = "application/json")
    public ResponseEntity<List<AttachmentDto>> getAttachmentsByCarId(@PathVariable Long id) {
        try {
            List<PdfAttachment> attachments = carService.getAttachmentsByCarId(id);
            List<AttachmentDto> attachmentDtos = attachments.stream()
                    .map(this::convertToAttachmentDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(attachmentDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods
    private CarDto convertToCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getCarType(),
                car.getClientNumber(),
                car.getRepairs() != null ? car.getRepairs().stream()
                        .map(repair -> new RepairDto(
                                repair.getId(),
                                repair.getRepairType(),
                                repair.getCost(),
                                new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairRequestDate()),
                                repair.getRepairDate() != null ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairDate()) : null
                        ))
                        .collect(Collectors.toList()) : List.of(),
                car.getTotalRepairCost(),
                car.getAttachments() != null ? car.getAttachments().stream()
                        .map(attachment -> new AttachmentDto(
                                attachment.getId(),
                                attachment.getFileName(),
                                attachment.getFilePath()
                        ))
                        .collect(Collectors.toList()) : List.of(),
                car.getRepairRequestDate()
        );
    }

    private AttachmentDto convertToAttachmentDto(PdfAttachment attachment) {
        return new AttachmentDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFilePath()
        );
    }
}
