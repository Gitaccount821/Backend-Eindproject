package nl.novi.eindprojectbackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.Part;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class RepairDto {

    private Long id;

    @NotNull(message = "Repair type ID cannot be null")
    private Long repairTypeId;

    private String repairTypeName;
    private Double repairTypeCost;
    private Double totalRepairCost;

    @NotBlank(message = "Repair request date is required")
    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$", message = "Date must be in dd-MM-yyyy format")
    private String repairRequestDate;

    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$", message = "Date must be in dd-MM-yyyy format")
    private String repairDate;
    private List<Long> partIds;
    private List<PartDetailDto> partDetails;

    public RepairDto() {
    }

    public RepairDto(Long id, Long repairTypeId, String repairTypeName, Double repairTypeCost, Double totalRepairCost,
                     String repairRequestDate, String repairDate, List<Long> partIds, List<PartDetailDto> partDetails) {
        this.id = id;
        this.repairTypeId = repairTypeId;
        this.repairTypeName = repairTypeName;
        this.repairTypeCost = repairTypeCost;
        this.totalRepairCost = totalRepairCost;
        this.repairRequestDate = repairRequestDate;
        this.repairDate = repairDate;
        this.partIds = partIds;
        this.partDetails = partDetails;
    }

    public RepairDto(Repair repair) {
        this.id = repair.getId();
        this.repairTypeId = (repair.getRepairType() != null) ? repair.getRepairType().getId() : null;
        this.repairTypeName = (repair.getRepairType() != null) ? repair.getRepairType().getName() : null;
        this.repairTypeCost = (repair.getRepairType() != null) ? repair.getRepairType().getCost() : null;
        this.totalRepairCost = repair.getTotalRepairCost();
        this.repairRequestDate = (repair.getRepairRequestDate() != null)
                ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairRequestDate()) : null;
        this.repairDate = (repair.getRepairDate() != null)
                ? new SimpleDateFormat("dd-MM-yyyy").format(repair.getRepairDate()) : null;
        this.partIds = (repair.getParts() != null)
                ? repair.getParts().stream().map(Part::getId).collect(Collectors.toList()) : null;
        this.partDetails = (repair.getParts() != null)
                ? repair.getParts().stream()
                .map(part -> new PartDetailDto(
                        part.getId(),
                        part.getName(),
                        part.getPrice(),
                        part.getStock()
                ))
                .collect(Collectors.toList())
                : null;
    }

    // Allemaal nodig voor Carinformatie opvragen
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRepairTypeId() { return repairTypeId; }
    public void setRepairTypeId(Long repairTypeId) { this.repairTypeId = repairTypeId; }

    public String getRepairTypeName() { return repairTypeName; }
    public void setRepairTypeName(String repairTypeName) { this.repairTypeName = repairTypeName; }

    public Double getRepairTypeCost() { return repairTypeCost; }
    public void setRepairTypeCost(Double repairTypeCost) { this.repairTypeCost = repairTypeCost; }

    public Double getTotalRepairCost() { return totalRepairCost; }
    public void setTotalRepairCost(Double totalRepairCost) { this.totalRepairCost = totalRepairCost; }

    public String getRepairRequestDate() { return repairRequestDate; }
    public void setRepairRequestDate(String repairRequestDate) { this.repairRequestDate = repairRequestDate; }

    public String getRepairDate() { return repairDate; }
    public void setRepairDate(String repairDate) { this.repairDate = repairDate; }

    public List<Long> getPartIds() { return partIds; }
    public void setPartIds(List<Long> partIds) { this.partIds = partIds; }

    public List<PartDetailDto> getPartDetails() { return partDetails; }
    public void setPartDetails(List<PartDetailDto> partDetails) { this.partDetails = partDetails; }
}
