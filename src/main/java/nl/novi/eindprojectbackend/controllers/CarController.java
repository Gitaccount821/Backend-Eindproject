package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.services.CarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    // Endpoint nieuwe auto
    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        Car savedCar = carService.addCar(car);
        return ResponseEntity.ok(savedCar);
    }

    // Endpoint get all (auto)
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    // Endpoint auto via ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carService.getCarById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint update auto
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCar(id, car);
            return ResponseEntity.ok(updatedCar);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint delete auto via ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint add PDF (niet zeker of dit werkt)
    @PostMapping("/{id}/attachments")
    public ResponseEntity<PdfAttachment> addPdfAttachment(
            @PathVariable Long id,
            @RequestBody PdfAttachment attachment) {
        try {
            PdfAttachment savedAttachment = carService.addPdfAttachment(id, attachment);
            return ResponseEntity.ok(savedAttachment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint get PDF
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<PdfAttachment>> getAttachmentsByCarId(@PathVariable Long id) {
        try {
            List<PdfAttachment> attachments = carService.getAttachmentsByCarId(id);
            return ResponseEntity.ok(attachments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
