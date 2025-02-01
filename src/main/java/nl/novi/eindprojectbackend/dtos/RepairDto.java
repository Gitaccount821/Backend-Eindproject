package nl.novi.eindprojectbackend.dtos;

import java.util.List;

public class RepairDto {
    private Long id;
    private Long repairTypeId;
    private Double cost;
    private String repairRequestDate;
    private String repairDate;
    private List<Long> partIds;

    public RepairDto() {
    }

    public RepairDto(Long id, Long repairTypeId, Double cost, String repairRequestDate, String repairDate, List<Long> partIds) {
        this.id = id;
        this.repairTypeId = repairTypeId;
        this.cost = cost;
        this.repairRequestDate = repairRequestDate;
        this.repairDate = repairDate;
        this.partIds = partIds;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepairTypeId() {
        return repairTypeId;
    }

    public void setRepairTypeId(Long repairTypeId) {
        this.repairTypeId = repairTypeId;
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
