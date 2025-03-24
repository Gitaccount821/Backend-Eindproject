package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.AttachmentDto;
import nl.novi.eindprojectbackend.models.PdfAttachment;

public class PdfAttachmentMapper {

    private static final String DOWNLOAD_BASE_URL = "/api/pdfs/download/";

    private PdfAttachmentMapper() {}

    public static AttachmentDto toDto(PdfAttachment attachment) {
        return new AttachmentDto(
                attachment.getId(),
                attachment.getFileName()
        );
    }

    public static String buildDownloadUrl(PdfAttachment attachment) {
        return DOWNLOAD_BASE_URL + attachment.getCar().getId();
    }
}
