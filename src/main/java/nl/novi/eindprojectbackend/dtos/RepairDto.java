package nl.novi.eindprojectbackend.dtos;

import nl.novi.eindprojectbackend.models.Repair;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

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


    public RepairDto(Repair repair) {
        this.id = repair.getId();
        this.repairTypeId = (repair.getRepairType() != null) ? repair.getRepairType().getId() : null;
        this.cost = repair.getTotalRepairCost();
        this.repairRequestDate = (repair.getRepairRequestDate() != null)
                ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairRequestDate())
                : null;
        this.repairDate = (repair.getRepairDate() != null)
                ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairDate())
                : null;
        this.partIds = (repair.getParts() != null)
                ? repair.getParts().stream().map(part -> part.getId()).collect(Collectors.toList())
                : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRepairTypeId() { return repairTypeId; }
    public void setRepairTypeId(Long repairTypeId) { this.repairTypeId = repairTypeId; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getRepairRequestDate() { return repairRequestDate; }
    public void setRepairRequestDate(String repairRequestDate) { this.repairRequestDate = repairRequestDate; }

    public String getRepairDate() { return repairDate; }
    public void setRepairDate(String repairDate) { this.repairDate = repairDate; }

    public List<Long> getPartIds() { return partIds; }
    public void setPartIds(List<Long> partIds) { this.partIds = partIds; }
}
