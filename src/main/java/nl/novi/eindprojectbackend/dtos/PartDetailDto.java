package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.NotNull;

public class PartDetailDto {
    private Long id;


    private String name;

    @NotNull(message = "Price is required")
    private Double price;

    public PartDetailDto(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Ook hier de warnings ignoren, deze is nodig om de prijzen te zijn wanneer carinfo wordt aangevraagd per part
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
}
