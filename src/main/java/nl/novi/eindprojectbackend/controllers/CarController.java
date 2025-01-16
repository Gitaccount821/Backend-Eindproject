package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.services.CarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/cars", produces = "application/json")
public class CarController {

    @Autowired
    private CarService carService;


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<CarDto> addCar(@RequestBody Car car) {
        Car savedCar = carService.addCar(car);
        CarDto carDto = convertToCarDto(savedCar);
        return ResponseEntity.ok(carDto);
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


    private CarDto convertToCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getCarType(),
                car.getClientNumber(),
                car.getRepairDate() != null ? car.getRepairDate().toString() : null,
                car.getRepairs() != null ? car.getRepairs().stream()
                        .map(this::convertToRepairDto)
                        .collect(Collectors.toList()) : List.of(),
                car.getTotalRepairCost(),
                car.getAttachments() != null ? car.getAttachments().stream()
                        .map(this::convertToAttachmentDto)
                        .collect(Collectors.toList()) : List.of()
        );
    }


    private RepairDto convertToRepairDto(Repair repair) {
        return new RepairDto(
                repair.getId(),
                repair.getRepairType(),
                repair.getCost(),
                repair.getDate() != null ? repair.getDate().toString() : null
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
