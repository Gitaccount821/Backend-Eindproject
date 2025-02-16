package nl.novi.eindprojectbackend.repositories;

import nl.novi.eindprojectbackend.models.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
}
