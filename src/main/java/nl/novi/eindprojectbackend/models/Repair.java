package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repairType;
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private Date repairRequestDate;

    private Date repairDate;


    public Repair() {
    }


    public Repair(String repairType, Double cost, Car car) {
        this.repairType = repairType;
        this.cost = cost;
        this.car = car;
    }

    // Getters and setters
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

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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
