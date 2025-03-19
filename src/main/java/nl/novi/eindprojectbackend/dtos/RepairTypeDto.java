package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RepairTypeDto {

    private Long id;

    @NotBlank(message = "Repair type name is required.")
    private String name;

    @NotNull(message = "Repair type cost is required.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Repair type cost must be greater than zero.")
    private Double cost;

    @NotBlank(message = "Repair type description is required.")
    private String description;

    public RepairTypeDto() {
    }

    public RepairTypeDto(Long id, String name, Double cost, String description) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.description = description;
    }

    //nodig voor carinformatie
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getCost() { return cost; }
    public String getDescription() { return description; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCost(Double cost) { this.cost = cost; }
    public void setDescription(String description) { this.description = description; }
}
