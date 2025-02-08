package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.RepairTypeDto;
import nl.novi.eindprojectbackend.models.RepairType;

public class RepairTypeMapper {

    public static RepairTypeDto toDto(RepairType repairType) {
        return new RepairTypeDto(
                repairType.getId(),
                repairType.getName(),
                repairType.getCost(),
                repairType.getDescription()
        );
    }

    public static RepairType toEntity(RepairTypeDto dto) {
        RepairType repairType = new RepairType();
        repairType.setId(dto.getId());
        repairType.setName(dto.getName());
        repairType.setCost(dto.getCost());
        repairType.setDescription(dto.getDescription());
        return repairType;
    }
}
