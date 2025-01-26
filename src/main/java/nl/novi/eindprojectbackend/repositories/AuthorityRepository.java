package nl.novi.eindprojectbackend.repositories;

import nl.novi.eindprojectbackend.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
