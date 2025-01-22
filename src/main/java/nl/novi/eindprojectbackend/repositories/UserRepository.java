package nl.novi.eindprojectbackend.repositories;

import nl.novi.eindprojectbackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
