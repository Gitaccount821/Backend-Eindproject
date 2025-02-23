package nl.novi.eindprojectbackend.controllers;

import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.exceptions.*;
import nl.novi.eindprojectbackend.mappers.PdfAttachmentMapper;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.services.CarService;
import nl.novi.eindprojectbackend.services.PdfAttachmentService;
import nl.novi.eindprojectbackend.services.CustomUserDetailsService;

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

    private String getUserRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("Unknown Role");
    }

    private final PdfAttachmentService pdfAttachmentService;
    private final CarService carService;
    private final CustomUserDetailsService userDetailsService;

    public PdfController(PdfAttachmentService pdfAttachmentService, CarService carService, CustomUserDetailsService userDetailsService) {
        this.pdfAttachmentService = pdfAttachmentService;
        this.carService = carService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/upload/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentDto> uploadPdf(@PathVariable Long carId, @RequestParam("file") MultipartFile file) {
        try {
            PdfAttachment uploadedFile = pdfAttachmentService.uploadPdf(carId, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(PdfAttachmentMapper.toDto(uploadedFile));
        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Car", carId);
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Error uploading PDF.");
        }
    }





    @GetMapping("/{carId}")
    public ResponseEntity<AttachmentDto> getAttachmentByCarId(@PathVariable Long carId) {

        carService.getCarById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));


        PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId)
                .orElseThrow(() -> new RecordNotFoundException("PDF attachment", carId));

        return ResponseEntity.ok(PdfAttachmentMapper.toDto(attachment));
    }

    @GetMapping("/download/{carId}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long carId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userRole = getUserRole(auth);
        String username = auth.getName();

        Car car = carService.getCarById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));

        if (userRole.equals("KLANT") && !car.getOwner().getUsername().equals(username)) {
            throw new ForbiddenActionException(userRole, "access this car's PDF");
        }

        PdfAttachment attachment = pdfAttachmentService.getAttachmentByCarId(carId)
                .orElseThrow(() -> new RecordNotFoundException("PDF attachment", carId));

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BadRequestException("File not found or unreadable.");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);
        } catch (RecordNotFoundException e) {
            throw e;
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerException("Unexpected error while downloading PDF.");
        }
    }



    @DeleteMapping("/{carId}/{pdfId}")
    public ResponseEntity<?> deletePdf(@PathVariable Long carId, @PathVariable Long pdfId) {

        carService.getCarById(carId)
                .orElseThrow(() -> new RecordNotFoundException("Car", carId));


        pdfAttachmentService.getAttachmentByCarId(carId)
                .orElseThrow(() -> new RecordNotFoundException("PDF attachment", carId));

        pdfAttachmentService.deleteAttachment(pdfId);
        return ResponseEntity.ok("PDF deleted successfully.");
    }
}
