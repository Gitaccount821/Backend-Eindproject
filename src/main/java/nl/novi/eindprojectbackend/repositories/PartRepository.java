package nl.novi.eindprojectbackend.repositories;

import nl.novi.eindprojectbackend.models.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {}