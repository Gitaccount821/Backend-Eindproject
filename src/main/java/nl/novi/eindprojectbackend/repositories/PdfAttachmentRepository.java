package nl.novi.eindprojectbackend.repositories;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PdfAttachmentRepository extends JpaRepository<PdfAttachment, Long> {
    Optional<PdfAttachment> findByCarId(Long carId);
}