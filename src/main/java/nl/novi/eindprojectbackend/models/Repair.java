package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;

@Entity
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String repairType;
    private Double cost;

    // Many-to-one association with Car
    @ManyToOne
    @JoinColumn(name = "car_id")  // Foreign key to associate with Car
    private Car car;

    // Constructor with car
    public Repair(String repairType, Double cost, Car car) {
        this.repairType = repairType;
        this.cost = cost;
        this.car = car;
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

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
