package uz.solarnature.solarnaturebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.solarnature.solarnaturebot.domain.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    default Document findOne(Long id) {
        return findById(id).orElseThrow(
                () -> new RuntimeException("Document not found!")
        );
    }

}
