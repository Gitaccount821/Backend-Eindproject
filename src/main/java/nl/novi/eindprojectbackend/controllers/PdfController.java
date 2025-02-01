package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.services.PdfAttachmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/pdfs")
public class PdfController {

    @Autowired
    private PdfAttachmentService pdfAttachmentService;


    @PostMapping(value = "/upload/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDto> uploadPdf(@PathVariable Long carId, @RequestParam("file") MultipartFile file) {
        try {
            PdfAttachment uploadedFile = pdfAttachmentService.uploadPdf(carId, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AttachmentDto(uploadedFile.getId(), uploadedFile.getFileName(), uploadedFile.getFilePath()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/{carId}")
    public ResponseEntity<AttachmentDto> getAttachmentByCarId(@PathVariable Long carId) {
        try {
            PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId)
                    .orElseThrow(() -> new IllegalArgumentException("No PDF found for this car"));
            return ResponseEntity.ok(new AttachmentDto(
                    attachment.getId(),
                    attachment.getFileName(),
                    attachment.getFilePath()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/download/{carId}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long carId) {
        try {
            PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId)
                    .orElseThrow(() -> new IllegalArgumentException("No PDF found for this car"));

            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or unreadable!");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @DeleteMapping("/{carId}/{pdfId}")
    public ResponseEntity<Void> deletePdf(@PathVariable Long carId, @PathVariable Long pdfId) {
        try {
            pdfAttachmentService.deleteAttachment(pdfId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
