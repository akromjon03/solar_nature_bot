package uz.solarnature.solarnaturebot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.solarnature.solarnaturebot.domain.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @NotNull
    Document getById(@NotNull Long id);

}
