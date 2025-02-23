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

    @ManyToOne
    @JoinColumn(name = "owner_username", referencedColumnName = "username", nullable = false)
    private User owner;

    private String repairRequestDate;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Repair> repairs = new ArrayList<>();

    @Column(nullable = false)
    private Double totalRepairCost = 0.0;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private PdfAttachment pdfAttachment;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getRepairRequestDate() { return repairRequestDate; }
    public void setRepairRequestDate(String repairRequestDate) { this.repairRequestDate = repairRequestDate; }

    public List<Repair> getRepairs() { return repairs; }
    public void setRepairs(List<Repair> repairs) { this.repairs = repairs; }

    public Double getTotalRepairCost() { return totalRepairCost; }
    public void setTotalRepairCost(Double totalRepairCost) { this.totalRepairCost = totalRepairCost; }

    public PdfAttachment getPdfAttachment() { return pdfAttachment; }
    public void setPdfAttachment(PdfAttachment pdfAttachment) { this.pdfAttachment = pdfAttachment; }

    public void updateTotalRepairCost() {
        this.totalRepairCost = repairs.stream()
                .mapToDouble(repair -> {
                    double baseCost = (repair.getRepairType() != null) ? repair.getRepairType().getCost() : 0.0;
                    double partsCost = repair.getParts() != null
                            ? repair.getParts().stream().mapToDouble(Part::getPrice).sum()
                            : 0.0;
                    return baseCost + partsCost;
                })
                .sum();
    }
}
