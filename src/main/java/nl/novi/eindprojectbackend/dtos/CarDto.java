package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import nl.novi.eindprojectbackend.models.Car;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import java.util.stream.Collectors;

public class CarDto {
    private Long id;

    @NotBlank(message = "Car type cannot be empty")
    @Size(max = 100, message = "Car type cannot exceed 100 characters")
    private String carType;

    private String ownerUsername;
    private List<RepairDto> repairs;
    private Double totalRepairCost;
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
                  List<RepairDto> repairs, Double totalRepairCost, String repairRequestDate) {
        this.id = id;
        this.carType = carType;
        this.ownerUsername = ownerUsername;
        this.repairs = repairs;
        this.totalRepairCost = totalRepairCost;
        this.repairRequestDate = repairRequestDate;
    }

    public Long getId() { return id; }
    public String getCarType() { return carType; }
    public String getOwnerUsername() { return ownerUsername; }
    public List<RepairDto> getRepairs() { return repairs; }
    public Double getTotalRepairCost() { return totalRepairCost; }
    public String getRepairRequestDate() { return repairRequestDate; }

    public void setCarType(String carType) { this.carType = carType; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
    public void setRepairs(List<RepairDto> repairs) { this.repairs = repairs; }
    public void setTotalRepairCost(Double totalRepairCost) { this.totalRepairCost = totalRepairCost; }
    public void setRepairRequestDate(String repairRequestDate) { this.repairRequestDate = repairRequestDate; }
}
