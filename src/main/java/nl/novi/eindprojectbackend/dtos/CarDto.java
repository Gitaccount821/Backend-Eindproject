package nl.novi.eindprojectbackend.dtos;

import nl.novi.eindprojectbackend.models.Car;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CarDto {
    private Long id;
    private String carType;
    private String clientNumber;
    private List<RepairDto> repairs;
    private Double totalRepairCost;
    private List<AttachmentDto> attachments;
    private String repairRequestDate;


    public CarDto() {

    }


    public CarDto(Car car) {
        this.id = car.getId();
        this.carType = car.getCarType();
        this.clientNumber = car.getClientNumber();
        this.repairs = (car.getRepairs() != null)
                ? car.getRepairs().stream()
                .map(repair -> {

                    String repairRequestDateStr = (repair.getRepairRequestDate() != null)
                            ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairRequestDate())
                            : null;
                    return new RepairDto(
                            repair.getId(),
                            repair.getRepairType(),
                            repair.getCost(),
                            repairRequestDateStr,
                            null
                    );
                })
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
        this.repairRequestDate = car.getRepairRequestDate();
    }


    @JsonCreator
    public CarDto(Long id, String carType, String clientNumber,
                  List<RepairDto> repairs, Double totalRepairCost,
                  List<AttachmentDto> attachments, String repairRequestDate) {
        this.id = id;
        this.carType = carType;
        this.clientNumber = clientNumber;
        this.repairs = repairs;
        this.totalRepairCost = totalRepairCost;
        this.attachments = attachments;
        this.repairRequestDate = repairRequestDate;
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

    public String getRepairRequestDate() {
        return repairRequestDate;
    }

    public void setRepairRequestDate(String repairRequestDate) {
        this.repairRequestDate = repairRequestDate;
    }
}
