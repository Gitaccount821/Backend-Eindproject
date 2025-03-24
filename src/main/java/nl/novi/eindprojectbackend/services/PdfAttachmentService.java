package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.ForbiddenActionException;
import nl.novi.eindprojectbackend.exceptions.InternalServerException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class PdfAttachmentService {

    private static final String UPLOAD_DIR = "uploads/pdf/";

    private final PdfAttachmentRepository pdfAttachmentRepository;
    private final CarRepository carRepository;

    public PdfAttachmentService(PdfAttachmentRepository pdfAttachmentRepository, CarRepository carRepository) {
        this.pdfAttachmentRepository = pdfAttachmentRepository;
        this.carRepository = carRepository;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new InternalServerException("Failed to create upload directory.");
        }
    }

    public PdfAttachment uploadPdf(Long carId, MultipartFile file) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InternalServerException("Failed to store file.");
        }

        PdfAttachment attachment = new PdfAttachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFilePath(filePath.toString());
        attachment.setCar(car);

        return pdfAttachmentRepository.save(attachment);
    }

    public PdfAttachment getAttachmentByCarId(Long carId) {
        return pdfAttachmentRepository.findByCarId(carId)
                .orElseThrow(() -> new RecordNotFoundException("PDF Attachment for Car ID", carId));
    }

    public void deletePdfForCar(Long carId, Long pdfId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));

        PdfAttachment attachment = getAttachmentByCarId(carId);

        if (!attachment.getId().equals(pdfId)) {
            throw new BadRequestException("PDF ID does not match the car's attachment.");
        }

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            throw new InternalServerException("Failed to delete file.");
        }

        pdfAttachmentRepository.deleteById(pdfId);
    }

    public Resource downloadPdf(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));

        checkKlantDownloadPermission(car);

        PdfAttachment attachment = getAttachmentByCarId(carId);

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BadRequestException("File not found or unreadable.");
            }

            return resource;
        } catch (IOException e) {
            throw new InternalServerException("Unexpected error while accessing PDF.");
        }
    }

    private void checkKlantDownloadPermission(Car car) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = getUserRole(auth);
        String username = auth.getName();

        if ("KLANT".equals(userRole) && !car.getOwner().getUsername().equals(username)) {
            throw new ForbiddenActionException();
        }
    }

    private String getUserRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("Unknown");
    }
}
