package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RepairTypeDto {

    private Long id;

    @NotBlank(message = "Repair type name cannot be empty")
    @Size(max = 100, message = "Repair type name cannot exceed 100 characters")
    private String name;

    @Min(value = 1, message = "Cost must be at least 1")
    private Double cost;

    private String description;

    public RepairTypeDto(Long id, String name, Double cost, String description) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getCost() { return cost; }
    public String getDescription() { return description; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCost(Double cost) { this.cost = cost; }
    public void setDescription(String description) { this.description = description; }
}
