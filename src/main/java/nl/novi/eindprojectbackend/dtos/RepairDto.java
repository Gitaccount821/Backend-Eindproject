package nl.novi.eindprojectbackend.dtos;

public class RepairDto {
    private Long id;
    private String repairType;
    private Double cost;

    public RepairDto(Long id, String repairType, Double cost) {
        this.id = id;
        this.repairType = repairType;
        this.cost = cost;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}

