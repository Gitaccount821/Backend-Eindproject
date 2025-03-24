package nl.novi.eindprojectbackend.repositories;

import nl.novi.eindprojectbackend.models.RepairType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepairTypeRepository extends JpaRepository<RepairType, Long> {

    boolean existsByName(String name);
}
