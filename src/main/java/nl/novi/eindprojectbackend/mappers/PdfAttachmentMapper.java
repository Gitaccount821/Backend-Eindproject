package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.models.PdfAttachment;

public class PdfAttachmentMapper {

    public static AttachmentDto toDto(PdfAttachment pdfAttachment) {
        return new AttachmentDto(
                pdfAttachment.getId(),
                pdfAttachment.getFileName(),
                pdfAttachment.getFilePath()
        );
    }

    public static PdfAttachment toEntity(AttachmentDto dto) {
        PdfAttachment pdfAttachment = new PdfAttachment();
        pdfAttachment.setId(dto.getId());
        pdfAttachment.setFileName(dto.getFileName());
        pdfAttachment.setFilePath(dto.getFilePath());
        return pdfAttachment;
    }
}
