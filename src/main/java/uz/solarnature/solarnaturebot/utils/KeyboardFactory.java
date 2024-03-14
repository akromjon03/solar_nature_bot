package uz.solarnature.solarnaturebot.utils;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;

import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboard getLanguageKeyboard() {
        var enButton = button(UserLanguage.ENGLISH.name() + "🇬🇧", UserLanguage.ENGLISH.name());
        var ruButton = button(UserLanguage.RUSSIAN.name() + "\uD83C\uDDF7\uD83C\uDDFA", UserLanguage.RUSSIAN.name());
        var uzButton = button(UserLanguage.UZBEK.name() + "\uD83C\uDDFA\uD83C\uDDFF", UserLanguage.UZBEK.name());

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

    public static ReplyKeyboard getPhoneKeyboard() {
        var button = new KeyboardButton(MessageUtil.getMessage("share.phone"));
        button.setRequestContact(true);
        var row = new KeyboardRow();
        row.add(button);
        return ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .build();
    }
}
