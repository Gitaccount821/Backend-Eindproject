package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.exceptions.BadRequestException;
import nl.novi.eindprojectbackend.exceptions.CarNotFoundException;
import nl.novi.eindprojectbackend.exceptions.PdfNotFoundException;
import nl.novi.eindprojectbackend.mappers.PdfAttachmentMapper;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.services.PdfAttachmentService;

import org.springframework.http.*;
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
    public ResponseEntity<AttachmentDto> uploadPdf(@PathVariable Long carId, @RequestParam("file") MultipartFile file) {
        try {

            PdfAttachment uploadedFile = pdfAttachmentService.uploadPdf(carId, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PdfAttachmentMapper.toDto(uploadedFile));
        } catch (CarNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{carId}")
    public ResponseEntity<AttachmentDto> getAttachmentByCarId(@PathVariable Long carId) {
        try {

            PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId)
                    .orElseThrow(PdfNotFoundException::new);
            return ResponseEntity.ok(PdfAttachmentMapper.toDto(attachment));
        } catch (CarNotFoundException | PdfNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download/{carId}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long carId) {
        try {
            PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId)
                    .orElseThrow(PdfNotFoundException::new);

            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or unreadable!");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);

        } catch (PdfNotFoundException | CarNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{carId}/{pdfId}")
    public ResponseEntity<Void> deletePdf(@PathVariable Long carId, @PathVariable Long pdfId) {
        try {

            pdfAttachmentService.deleteAttachment(pdfId);
            return ResponseEntity.noContent().build();
        } catch (CarNotFoundException | PdfNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
