package nl.novi.eindprojectbackend.dtos;


public class AttachmentDto {
    private Long id;

    private String fileName;

    private String filePath;

    public AttachmentDto(Long id, String fileName, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }


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
