package nl.novi.eindprojectbackend.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;



public class CarDto {
    private Long id;
    private String carType;
    private String ownerUsername;
    private List<RepairDto> repairs;
    private Double totalRepairCost;
    private String repairRequestDate;
    private Double totalPriceWithBTW;

    // dto voor testing
    public CarDto(){
    }

    @JsonCreator
    public CarDto(Long id, String carType, String ownerUsername,
                  List<RepairDto> repairs, Double totalRepairCost, String repairRequestDate, Double totalPriceWithBTW) {
        this.id = id;
        this.carType = carType;
        this.ownerUsername = ownerUsername;
        this.repairs = repairs;
        this.totalRepairCost = totalRepairCost;
        this.repairRequestDate = repairRequestDate;
        this.totalPriceWithBTW = totalPriceWithBTW;
    }



    // Veel warnings hier dat er geen usages zijn, maar het weghalen breekt wel de code van Car informatie (reperatie). Ignoring on purpose.
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
    public Double getTotalPriceWithBTW() { return totalPriceWithBTW; }
    public void setTotalPriceWithBTW(Double totalPriceWithBTW) { this.totalPriceWithBTW = totalPriceWithBTW; }
}
