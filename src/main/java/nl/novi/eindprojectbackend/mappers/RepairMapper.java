package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.models.Repair;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepairMapper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private RepairMapper() {
    }

    public static RepairDto toDto(Repair repair) {
        if (repair == null) {
            return null;
        }

        RepairDto dto = new RepairDto();
        dto.setId(repair.getId());

        if (repair.getRepairType() != null) {
            dto.setRepairTypeId(repair.getRepairType().getId());
            dto.setRepairTypeName(repair.getRepairType().getName());
            dto.setRepairTypeCost(repair.getRepairType().getCost());
        }

        dto.setRepairRequestDate(repair.getRepairRequestDate() != null
                ? DATE_FORMAT.format(repair.getRepairRequestDate()) : null);

        dto.setRepairDate(repair.getRepairDate() != null
                ? DATE_FORMAT.format(repair.getRepairDate()) : null);

        dto.setTotalRepairCost(repair.getTotalRepairCost());

        List<Part> parts = repair.getParts() != null ? repair.getParts() : Collections.emptyList();

        dto.setPartIds(parts.stream()
                .map(Part::getId)
                .collect(Collectors.toList()));

        dto.setPartDetails(parts.stream()
                .map(part -> new PartDetailDto(
                        part.getId(),
                        part.getName(),
                        part.getPrice(),
                        part.getStock()
                ))
                .collect(Collectors.toList()));

        return dto;
    }

    public static Repair toEntity(RepairDto dto) {
        if (dto == null) {
            return null;
        }
        Repair repair = new Repair();
        repair.setId(dto.getId());
        return repair;
    }
}
