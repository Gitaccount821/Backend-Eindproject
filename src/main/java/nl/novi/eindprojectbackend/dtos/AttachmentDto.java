package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AttachmentDto {
    private Long id;

    @NotBlank(message = "File name cannot be empty")
    @Size(max = 100, message = "File name cannot exceed 100 characters")
    private String fileName;

    @NotBlank(message = "File path cannot be empty")
    @Size(max = 255, message = "File path cannot exceed 255 characters")
    private String filePath;

    public AttachmentDto(Long id, String fileName, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }



    public String getFilePath() {
        return filePath;
    }

}
