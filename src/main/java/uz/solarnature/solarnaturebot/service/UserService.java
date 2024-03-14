package uz.solarnature.solarnaturebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.solarnature.solarnaturebot.domain.entity.User;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createFromTgUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        var user = User.newInstance(
                tgUser.getId(),
                tgUser.getFirstName(),
                UserLanguage.fromCode(tgUser.getLanguageCode())
        );
        userRepository.save(user);
        return user;
    }

}
