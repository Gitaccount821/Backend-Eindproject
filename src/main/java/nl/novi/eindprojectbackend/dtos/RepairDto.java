package nl.novi.eindprojectbackend.dtos;

import java.time.LocalDateTime;

public class RepairDto {
    private Long id;
    private String repairType;
    private Double cost;
    private String date;


    public RepairDto(Long id, String repairType, Double cost, String date) {
        this.id = id;
        this.repairType = repairType;
        this.cost = cost;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
