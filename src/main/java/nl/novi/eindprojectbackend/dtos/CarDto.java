package nl.novi.eindprojectbackend.dtos;

import nl.novi.eindprojectbackend.models.Car;

import java.util.List;
import java.util.stream.Collectors;

public class CarDto {
    private Long id;
    private String carType;
    private String clientNumber;
    private String repairDate;
    private List<RepairDto> repairs; // Updated to use RepairDto
    private Double totalRepairCost;
    private List<AttachmentDto> attachments;

    public CarDto(Car car) {
        this.id = car.getId();
        this.carType = car.getCarType();
        this.clientNumber = car.getClientNumber();
        this.repairDate = car.getRepairDate() != null ? car.getRepairDate().toString() : null; // Null-safe handling
        this.repairs = (car.getRepairs() != null)
                ? car.getRepairs().stream()
                .map(repair -> new RepairDto(
                        repair.getId(),
                        repair.getRepairType(),
                        repair.getCost(),
                        repair.getDate() != null ? repair.getDate().toString() : null // Ensure that date is handled properly
                ))
                .collect(Collectors.toList())
                : List.of();
        this.totalRepairCost = car.getTotalRepairCost();
        this.attachments = car.getAttachments() != null
                ? car.getAttachments().stream()
                .map(attachment -> new AttachmentDto(
                        attachment.getId(),
                        attachment.getFileName(),
                        attachment.getFilePath()
                ))
                .collect(Collectors.toList())
                : List.of(); // Null-safe handling
    }

    public CarDto(Long id, String carType, String clientNumber, String repairDate,
                  List<RepairDto> repairs, Double totalRepairCost, List<AttachmentDto> attachments) {
        this.id = id;
        this.carType = carType;
        this.clientNumber = clientNumber;
        this.repairDate = repairDate;
        this.repairs = repairs;
        this.totalRepairCost = totalRepairCost;
        this.attachments = attachments;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(String repairDate) {
        this.repairDate = repairDate;
    }

    public List<RepairDto> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<RepairDto> repairs) {
        this.repairs = repairs;
    }

    public Double getTotalRepairCost() {
        return totalRepairCost;
    }

    public void setTotalRepairCost(Double totalRepairCost) {
        this.totalRepairCost = totalRepairCost;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDto> attachments) {
        this.attachments = attachments;
    }
}
