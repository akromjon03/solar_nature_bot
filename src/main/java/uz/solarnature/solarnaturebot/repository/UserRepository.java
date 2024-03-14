package uz.solarnature.solarnaturebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.solarnature.solarnaturebot.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByChatId(Long chatId);

}
