package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class PdfAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "File name cannot be empty")
    private String fileName;

    @NotBlank(message = "File path cannot be empty")
    private String filePath;

    @OneToOne
    @JoinColumn(name = "car_id", unique = true, nullable = false)
    private Car car;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
}
