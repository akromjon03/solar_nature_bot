package uz.solarnature.solarnaturebot.domain.enumeration;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum UserLanguage {
    ENGLISH("en"),
    RUSSIAN("ru"),
    UZBEK("uz");

    private final String code;
    private final Locale locale;

    UserLanguage(String code) {
        this.code = code;
        this.locale = new Locale(code);
    }

    public static UserLanguage fromCode(String code) {
        return switch (code) {
            case "en" -> ENGLISH;
            case "uz" -> UZBEK;
            default -> RUSSIAN;
        };
    }

}
