package uz.solarnature.solarnaturebot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;

import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getLanguageKeyboard() {
        var enButton = button(UserLanguage.ENGLISH.getText(), UserLanguage.ENGLISH.getText());
        var ruButton = button(UserLanguage.RUSSIAN.getText(), UserLanguage.RUSSIAN.getText());
        var uzButton = button(UserLanguage.UZBEK.getText(), UserLanguage.UZBEK.getText());

        var keyboard = List.of(
                List.of(enButton),
                List.of(ruButton),
                List.of(uzButton)
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardButton button(String text, String data) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(data)
                .build();
    }

}
