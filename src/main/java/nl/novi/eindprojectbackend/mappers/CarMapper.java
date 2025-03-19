package nl.novi.eindprojectbackend.mappers;

import nl.novi.eindprojectbackend.dtos.CarDto;
import nl.novi.eindprojectbackend.dtos.PartDetailDto;
import nl.novi.eindprojectbackend.dtos.RepairDto;
import nl.novi.eindprojectbackend.models.Car;
import nl.novi.eindprojectbackend.models.Part;
import nl.novi.eindprojectbackend.models.Repair;
import nl.novi.eindprojectbackend.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    private static final double VAT_RATE = 1.21;


    public static CarDto toDto(Car car) {
        if (car == null) {
            return null;
        }

        double totalRepairCost = car.getTotalRepairCost() != null ? car.getTotalRepairCost() : 0.0;
        double totalPriceWithBTW = totalRepairCost * VAT_RATE;

        return new CarDto(
                car.getId(),
                car.getCarType(),
                car.getOwner().getUsername(),
                car.getRepairs() != null ? car.getRepairs().stream()
                        .map(CarMapper::mapRepairToDto)
                        .collect(Collectors.toList()) : List.of(),
                totalRepairCost,
                car.getRepairRequestDate(),
                totalPriceWithBTW
        );
    }


    public static Car toEntity(CarDto dto, User owner) {
        if (dto == null) {
            return null;
        }

        Car car = new Car();
        car.setId(dto.getId()); // Usually optional when creating a new entity
        car.setCarType(dto.getCarType());
        car.setRepairRequestDate(dto.getRepairRequestDate());
        car.setOwner(owner);

        return car;
    }


    private static RepairDto mapRepairToDto(Repair repair) {
        if (repair == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        return new RepairDto(
                repair.getId(),
                repair.getRepairType().getId(),
                repair.getRepairType().getName(),
                repair.getRepairType().getCost(),
                repair.getTotalRepairCost(),
                sdf.format(repair.getRepairRequestDate()),
                repair.getRepairDate() != null ? sdf.format(repair.getRepairDate()) : null,
                repair.getParts() != null ? repair.getParts().stream()
                        .map(Part::getId)
                        .collect(Collectors.toList()) : null,
                repair.getParts() != null ? repair.getParts().stream()
                        .map(part -> new PartDetailDto(
                                part.getId(),
                                part.getName(),
                                part.getPrice(),
                                part.getStock()
                        ))
                        .collect(Collectors.toList()) : null
        );
    }
}
