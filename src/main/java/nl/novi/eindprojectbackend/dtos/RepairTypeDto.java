package nl.novi.eindprojectbackend.dtos;

public class RepairTypeDto {

    private Long id;

    private String name;

    private Double cost;

    private String description;

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
