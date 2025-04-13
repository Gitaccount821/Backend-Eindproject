
package nl.novi.eindprojectbackend.controllers;
import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.mappers.PdfAttachmentMapper;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import nl.novi.eindprojectbackend.services.PdfAttachmentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;




        @RestController
        @RequestMapping("/api/cars/{carId}/pdf")
        public class PdfController {

            private final PdfAttachmentService pdfAttachmentService;

            public PdfController(PdfAttachmentService pdfAttachmentService) {
                this.pdfAttachmentService = pdfAttachmentService;
            }

            @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
            public ResponseEntity<AttachmentDto> uploadPdf(@PathVariable Long carId,
                                                           @RequestParam("file") MultipartFile file) {
                PdfAttachment uploadedFile = pdfAttachmentService.uploadPdf(carId, file);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(PdfAttachmentMapper.toDto(uploadedFile));
            }

            @GetMapping
            public ResponseEntity<Resource> downloadPdf(@PathVariable Long carId) {
                Resource resource = pdfAttachmentService.downloadPdf(carId);
                String filename = pdfAttachmentService.getAttachmentByCarId(carId).getFileName();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            }

            @DeleteMapping("/{pdfId}")
            public ResponseEntity<String> deletePdf(@PathVariable Long carId,
                                                    @PathVariable Long pdfId) {
                pdfAttachmentService.deletePdfForCar(carId, pdfId);
                return ResponseEntity.ok("PDF deleted successfully.");
            }
        }
