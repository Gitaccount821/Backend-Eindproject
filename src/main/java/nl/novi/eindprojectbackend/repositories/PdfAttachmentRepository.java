package nl.novi.eindprojectbackend.repositories;
import nl.novi.eindprojectbackend.models.PdfAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfAttachmentRepository extends JpaRepository<PdfAttachment, Long> {
    // NOG LEEG, voeg later hier custom queries toe indien ze nodig zijn!
}