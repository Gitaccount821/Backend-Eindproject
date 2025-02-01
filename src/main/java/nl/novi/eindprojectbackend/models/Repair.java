package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repair_type_id", nullable = false)
    private RepairType repairType;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToMany
    private List<Part> parts;

    private Double totalRepairCost;

    private Date repairRequestDate;
    private Date repairDate;

    public Repair() {
    }

    public Repair(RepairType repairType, Car car) {
        this.repairType = repairType;
        this.car = car;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public Double getTotalRepairCost() {
        return totalRepairCost;
    }

    public void setTotalRepairCost(Double totalRepairCost) {
        this.totalRepairCost = totalRepairCost;
    }

    public Date getRepairRequestDate() {
        return repairRequestDate;
    }

    public void setRepairRequestDate(Date repairRequestDate) {
        this.repairRequestDate = repairRequestDate;
    }

    public Date getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(Date repairDate) {
        this.repairDate = repairDate;
    }
}
