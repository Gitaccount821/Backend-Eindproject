package nl.novi.eindprojectbackend.services;

import nl.novi.eindprojectbackend.exceptions.InternalServerException;
import nl.novi.eindprojectbackend.exceptions.RecordNotFoundException;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.repositories.CarRepository;
import nl.novi.eindprojectbackend.repositories.PdfAttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class PdfAttachmentService {

    private static final String UPLOAD_DIR = "uploads/pdf/";

    private final PdfAttachmentRepository pdfAttachmentRepository;
    private final CarRepository carRepository;

    public PdfAttachmentService(PdfAttachmentRepository pdfAttachmentRepository, CarRepository carRepository) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new InternalServerException("Failed to create upload directory.");
        }
        this.pdfAttachmentRepository = pdfAttachmentRepository;
        this.carRepository = carRepository;
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

    public Optional<PdfAttachment> getAttachmentByCarId(Long carId) {
        return Optional.ofNullable(pdfAttachmentRepository.findByCarId(carId)
                .orElseThrow(() -> new RecordNotFoundException("PDF Attachment for Car ID", carId)));
    }

    public void deleteAttachment(Long id) {
        PdfAttachment attachment = pdfAttachmentRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("PDF Attachment", id));

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            throw new InternalServerException("Failed to delete file.");
        }

        pdfAttachmentRepository.deleteById(id);
    }
}
