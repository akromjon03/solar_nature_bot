package uz.solarnature.solarnaturebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.solarnature.solarnaturebot.domain.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
