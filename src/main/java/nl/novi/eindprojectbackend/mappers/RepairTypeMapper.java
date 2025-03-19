package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.RepairTypeDto;
import nl.novi.eindprojectbackend.models.RepairType;

public class RepairTypeMapper {

    private RepairTypeMapper() {
    }

    public static RepairTypeDto toDto(RepairType repairType) {
        if (repairType == null) {
            return null;
        }

        return new RepairTypeDto(
                repairType.getId(),
                repairType.getName(),
                repairType.getCost(),
                repairType.getDescription()
        );
    }

    public static RepairType toEntity(RepairTypeDto dto) {
        if (dto == null) {
            return null;
        }

        RepairType repairType = new RepairType();
        repairType.setId(dto.getId());
        repairType.setName(dto.getName());
        repairType.setCost(dto.getCost());
        repairType.setDescription(dto.getDescription());
        return repairType;
    }
}
