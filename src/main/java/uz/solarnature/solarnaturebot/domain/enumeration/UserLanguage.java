package uz.solarnature.solarnaturebot.domain.enumeration;

import lombok.Getter;

@Getter
public enum UserLanguage {
    ENGLISH,
    RUSSIAN,
    UZBEK;

    private final String text = this.name().toLowerCase();

}
