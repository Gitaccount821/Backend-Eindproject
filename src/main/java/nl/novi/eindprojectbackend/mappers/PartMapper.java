package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.models.Part;

public class PartMapper {

    public static PartDetailDto toDto(Part part) {
        if (part == null) {
            return null;
        }
        return new PartDetailDto(
                part.getId(),
                part.getName(),
                part.getPrice(),
                part.getStock()
        );
    }


    public static Part toEntity(PartDetailDto dto) {
        if (dto == null) {
            return null;
        }
        Part part = new Part();
        part.setId(dto.getId());
        part.setName(dto.getName());
        part.setPrice(dto.getPrice());
        part.setStock(dto.getStock());
        return part;
    }
}
