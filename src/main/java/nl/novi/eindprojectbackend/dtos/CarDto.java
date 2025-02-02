package nl.novi.eindprojectbackend.dtos;

import nl.novi.eindprojectbackend.models.Car;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import java.util.stream.Collectors;

public class CarDto {
    private Long id;
    private String carType;
    private String ownerUsername;
    private List<RepairDto> repairs;
    private Double totalRepairCost;
    private List<AttachmentDto> attachments;
    private String repairRequestDate;

    public CarDto(Car car) {
        this.id = car.getId();
        this.carType = car.getCarType();
        this.ownerUsername = (car.getOwner() != null) ? car.getOwner().getUsername() : null;
        this.totalRepairCost = car.getTotalRepairCost();
        this.repairRequestDate = car.getRepairRequestDate();
        this.repairs = car.getRepairs().stream()
                .map(RepairDto::new)
                .collect(Collectors.toList());
    }

    @JsonCreator
    public CarDto(Long id, String carType, String ownerUsername,
                  List<RepairDto> repairs, Double totalRepairCost,
                  List<AttachmentDto> attachments, String repairRequestDate) {
        this.id = id;
        this.carType = carType;
        this.ownerUsername = ownerUsername;
        this.repairs = repairs;
        this.totalRepairCost = totalRepairCost;
        this.attachments = attachments;
        this.repairRequestDate = repairRequestDate;
    }

    public Long getId() { return id; }
    public String getCarType() { return carType; }  // FIXED: Added missing method
    public String getOwnerUsername() { return ownerUsername; }
    public List<RepairDto> getRepairs() { return repairs; }
    public Double getTotalRepairCost() { return totalRepairCost; }
    public List<AttachmentDto> getAttachments() { return attachments; }
    public String getRepairRequestDate() { return repairRequestDate; } // FIXED: Added missing method


    public void setCarType(String carType) { this.carType = carType; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public void setRepairs(List<RepairDto> repairs) { this.repairs = repairs; }
    public void setTotalRepairCost(Double totalRepairCost) { this.totalRepairCost = totalRepairCost; }
    public void setAttachments(List<AttachmentDto> attachments) { this.attachments = attachments; }
    public void setRepairRequestDate(String repairRequestDate) { this.repairRequestDate = repairRequestDate; }
}
