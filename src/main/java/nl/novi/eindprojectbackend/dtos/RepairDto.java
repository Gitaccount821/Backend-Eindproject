package nl.novi.eindprojectbackend.dtos;

import java.util.List;

public class RepairDto {

    private Long id;
    private String repairType;
    private Double cost;
    private String repairRequestDate;
    private String repairDate;
    private List<Long> partIds;

    public RepairDto() {
    }

    public RepairDto(Long id, String repairType, Double cost, String repairRequestDate, String repairDate) {
        this.id = id;
        this.repairType = repairType;
        this.cost = cost;
        this.repairRequestDate = repairRequestDate;
        this.repairDate = repairDate;
        this.partIds = partIds;
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

    public String getRepairRequestDate() {
        return repairRequestDate;
    }

    public void setRepairRequestDate(String repairRequestDate) {
        this.repairRequestDate = repairRequestDate;
    }

    public String getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(String repairDate) {
        this.repairDate = repairDate;
    }

    public List<Long> getPartIds() {
        return partIds;
    }

    public void setPartIds(List<Long> partIds) {
        this.partIds = partIds;
    }
}
