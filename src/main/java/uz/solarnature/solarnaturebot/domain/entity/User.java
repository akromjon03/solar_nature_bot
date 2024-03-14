package uz.solarnature.solarnaturebot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "chat_id")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String phone;

    private String email;

    private String address;

    @Enumerated(EnumType.STRING)
    private UserLanguage language;

    private Long chatId;

    public static User newInstance(Long chatId, String fullName, UserLanguage language) {
        var user = new User();
        user.setChatId(chatId);
        user.setFullName(fullName);
        user.setLanguage(language);
        return user;
    }

}
