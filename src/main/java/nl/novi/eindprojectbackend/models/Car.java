package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carType;

    private String clientNumber;

    private String repairRequestDate;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repair> repairs = new ArrayList<>();

    private Double totalRepairCost;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PdfAttachment> attachments = new ArrayList<>();

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

    public String getRepairRequestDate() {
        return repairRequestDate;
    }

    public void setRepairRequestDate(String repairRequestDate) {
        this.repairRequestDate = repairRequestDate;
    }

    public List<Repair> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<Repair> repairs) {
        this.repairs = repairs;
    }

    public Double getTotalRepairCost() {
        return totalRepairCost;
    }

    public void setTotalRepairCost(Double totalRepairCost) {
        this.totalRepairCost = totalRepairCost;
    }

    public List<PdfAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PdfAttachment> attachments) {
        this.attachments = attachments;
    }
}
