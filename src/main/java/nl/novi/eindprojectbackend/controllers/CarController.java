package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.*;
import nl.novi.eindprojectbackend.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private PartService partService;

    @Autowired
    private RepairTypeService repairTypeService;

    @Autowired
    private PdfAttachmentService pdfAttachmentService;


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<CarDto> addCar(@RequestBody CarDto carDto) {
        if (carDto.getCarType() == null || carDto.getClientNumber() == null) {
            return ResponseEntity.badRequest().build();
        }

        Car car = new Car();
        car.setCarType(carDto.getCarType());
        car.setClientNumber(carDto.getClientNumber());

        if (carDto.getRepairRequestDate() != null) {
            car.setRepairRequestDate(carDto.getRepairRequestDate());
        }

        Car savedCar = carService.addCar(car);
        return ResponseEntity.ok(new CarDto(savedCar));
    }


    @GetMapping(produces = "application/json")
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        List<CarDto> carDtos = cars.stream().map(this::convertToCarDto).collect(Collectors.toList());
        return ResponseEntity.ok(carDtos);
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        return carService.getCarById(id)
                .map(car -> ResponseEntity.ok(convertToCarDto(car)))
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping(value = "/{carId}/repairs", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CarDto> addRepairToCar(@PathVariable Long carId, @RequestBody RepairDto repairDto) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date repairRequestDate = sdf.parse(repairDto.getRepairRequestDate());
            Date repairDate = repairDto.getRepairDate() != null ? sdf.parse(repairDto.getRepairDate()) : null;


            Car car = carService.getCarById(carId).orElseThrow(() -> new IllegalArgumentException("Car not found"));


            RepairType repairType = repairTypeService.getRepairTypeById(repairDto.getRepairTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Repair type not found"));


            Repair repair = new Repair();
            repair.setRepairType(repairType);
            repair.setRepairRequestDate(repairRequestDate);
            repair.setRepairDate(repairDate);
            repair.setCar(car);

            double totalCost = repairType.getCost();


            if (repairDto.getPartIds() != null && !repairDto.getPartIds().isEmpty()) {
                List<Part> parts = repairDto.getPartIds().stream()
                        .map(partId -> partService.getPartById(partId)
                                .orElseThrow(() -> new IllegalArgumentException("Part not found for ID: " + partId)))
                        .collect(Collectors.toList());

                repair.setParts(parts);

                for (Part part : parts) {
                    totalCost += part.getPrice();
                    part.setStock(part.getStock() - 1);
                    partService.updatePart(part);
                }
            }

            repair.setTotalRepairCost(totalCost);


            repairService.addRepair(repair);


            car.getRepairs().add(repair);
            car.updateTotalRepairCost();
            carService.addCar(car);

            return ResponseEntity.ok(new CarDto(car));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping(value = "/{carId}/attachments", consumes = "multipart/form-data")
    public ResponseEntity<AttachmentDto> uploadPdfAttachment(@PathVariable Long carId,
                                                             @RequestParam("file") MultipartFile file) {
        try {
            PdfAttachment savedAttachment = pdfAttachmentService.uploadPdf(carId, file);
            return ResponseEntity.ok(new AttachmentDto(
                    savedAttachment.getId(),
                    savedAttachment.getFileName(),
                    savedAttachment.getFilePath()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/{carId}/attachment", produces = "application/json")
    public ResponseEntity<AttachmentDto> getAttachmentByCarId(@PathVariable Long carId) {
        try {
            PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId);
            AttachmentDto attachmentDto = new AttachmentDto(
                    attachment.getId(),
                    attachment.getFileName(),
                    attachment.getFilePath()
            );
            return ResponseEntity.ok(attachmentDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @DeleteMapping("/{carId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long carId, @PathVariable Long attachmentId) {
        try {
            pdfAttachmentService.deleteAttachment(attachmentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private CarDto convertToCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getCarType(),
                car.getClientNumber(),
                car.getRepairs() != null ? car.getRepairs().stream()
                        .map(repair -> new RepairDto(
                                repair.getId(),
                                repair.getRepairType().getId(),
                                repair.getTotalRepairCost(),
                                new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairRequestDate()),
                                repair.getRepairDate() != null ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairDate()) : null,
                                repair.getParts() != null ? repair.getParts().stream().map(Part::getId).collect(Collectors.toList()) : null
                        ))
                        .collect(Collectors.toList()) : List.of(),
                car.getTotalRepairCost(),


                car.getPdfAttachment() != null
                        ? List.of(new AttachmentDto(
                        car.getPdfAttachment().getId(),
                        car.getPdfAttachment().getFileName(),
                        car.getPdfAttachment().getFilePath()
                ))
                        : List.of(),

                car.getRepairRequestDate()
        );
    }
}
