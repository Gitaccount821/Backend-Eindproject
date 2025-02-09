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


}
