package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.models.Repair;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    public static CarDto toDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getCarType(),
                car.getOwner().getUsername(),
                car.getRepairs() != null ? car.getRepairs().stream()
                        .map(CarMapper::mapRepairToDto)
                        .collect(Collectors.toList()) : List.of(),
                car.getTotalRepairCost(),
                car.getRepairRequestDate()
        );
    }

    private static RepairDto mapRepairToDto(Repair repair) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        return new RepairDto(
                repair.getId(),
                repair.getRepairType().getId(),
                repair.getRepairType().getName(),
                repair.getRepairType().getCost(),
                repair.getTotalRepairCost(),
                sdf.format(repair.getRepairRequestDate()),
                repair.getRepairDate() != null ? sdf.format(repair.getRepairDate()) : null,
                repair.getParts() != null ? repair.getParts().stream().map(Part::getId).collect(Collectors.toList()) : null,
                repair.getParts() != null ? repair.getParts().stream()
                        .map(part -> new PartDetailDto(part.getId(), part.getName(), part.getPrice()))
                        .collect(Collectors.toList()) : null
        );
    }
}
