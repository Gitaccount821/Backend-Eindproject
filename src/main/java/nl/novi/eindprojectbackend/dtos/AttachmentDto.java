package nl.novi.eindprojectbackend.dtos;

public class AttachmentDto {

    private Long id;
    private String fileName;
    private String downloadUrl;

    public AttachmentDto() {}

    public AttachmentDto(Long id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public Long getId() { return id; }
    public String getFileName() { return fileName; }

    public void setId(Long id) { this.id = id; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
