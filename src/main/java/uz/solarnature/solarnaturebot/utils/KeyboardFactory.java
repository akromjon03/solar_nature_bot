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
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getMenuKeyboard() {
        var menu1 = button("Vetrennoy elektrostansiyasi uchun anketa", "menu1" );
        var menu2 = button("To'liq xizmat ko'rsatish PV zavodi uchun faktlar varaqasi", "menu2" );
        var menu3 = button("Fotovoltaik stantsiyani tanlash uchun so'rovnoma", "menu3" );

        var keyboard = List.of(List.of(menu1),
                List.of(menu2),
                List.of(menu3));


        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
