package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String carType; //Car type

    private String clientNumber; // Klantnummer

    @Temporal(TemporalType.DATE)
    private Date repairDate; // repair datum

    @ElementCollection
    @CollectionTable(name = "car_repairs", joinColumns = @JoinColumn(name = "car_id"))
    @Column(name = "repair")
    private List<String> repairs; // lijst van de uitgevoerde repairs

    private Double totalRepairCost; // Total cost van repairs

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "car")
    private List<PdfAttachment> attachments = new ArrayList<>(); // pdf lijst gekoppeld aan car

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

    public Date getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(Date repairDate) {
        this.repairDate = repairDate;
    }

    public List<String> getRepairs() {
        return repairs;
    }

    public void setRepairs(List<String> repairs) {
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
