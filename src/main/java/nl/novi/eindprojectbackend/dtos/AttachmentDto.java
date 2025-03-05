package nl.novi.eindprojectbackend.dtos;


public class AttachmentDto {
    private Long id;

    public AttachmentDto(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
