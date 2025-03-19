

        package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.exceptions.*;
import nl.novi.eindprojectbackend.mappers.PdfAttachmentMapper;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.services.CarService;
import nl.novi.eindprojectbackend.services.PdfAttachmentService;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;



@RestController
@RequestMapping("/api/pdfs")
public class PdfController {

    private final PdfAttachmentService pdfAttachmentService;

    public PdfController(PdfAttachmentService pdfAttachmentService) {
        this.pdfAttachmentService = pdfAttachmentService;
    }

    @PostMapping(value = "/upload/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDto> uploadPdf(@PathVariable Long carId,
                                                   @RequestParam("file") MultipartFile file) {
        PdfAttachment uploadedFile = pdfAttachmentService.uploadPdf(carId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PdfAttachmentMapper.toDto(uploadedFile));
    }

    @GetMapping("/{carId}")
    public ResponseEntity<AttachmentDto> getAttachmentByCarId(@PathVariable Long carId) {
        PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId);
        return ResponseEntity.ok(PdfAttachmentMapper.toDto(attachment));
    }

    @GetMapping("/download/{carId}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long carId) {
        Resource resource = pdfAttachmentService.downloadPdf(carId);
        String filename = pdfAttachmentService.getAttachmentByCarId(carId).getFileName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @DeleteMapping("/{carId}/{pdfId}")
    public ResponseEntity<String> deletePdf(@PathVariable Long carId,
                                            @PathVariable Long pdfId) {
        pdfAttachmentService.deletePdfForCar(carId, pdfId);
        return ResponseEntity.ok("PDF deleted successfully.");
    }
}
