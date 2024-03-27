package uz.solarnature.solarnaturebot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.domain.enumeration.types.GeneralType;

import java.util.ArrayList;
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
        var button = new KeyboardButton(MessageUtil.getMessage("doc.phone.button"));
        button.setRequestContact(true);


        var row = new KeyboardRow();
        row.add(button);
        return ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .isPersistent(false)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getMenuKeyboard() {
        var create = new KeyboardButton(MessageUtil.getMessage("menu.create"));
        var about = new KeyboardButton(MessageUtil.getMessage("menu.about"));
        var feedback = new KeyboardButton(MessageUtil.getMessage("menu.feedback"));
        var lang = new KeyboardButton(MessageUtil.getMessage("menu.lang"));

        var keyboard = List.of(
                new KeyboardRow(List.of(create)),
                new KeyboardRow(List.of(about)),
                new KeyboardRow(List.of(feedback)),
                new KeyboardRow(List.of(lang))
        );


        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .oneTimeKeyboard(true)
                .isPersistent(false)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getAccountKeyboard() {
        var commercial = button(MessageUtil.getMessage("doc.account.commercial"), "commercial");
        var privateType = button(MessageUtil.getMessage("doc.account.private"), "private");

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(commercial, privateType))
                .build();
    }

    public static ReplyKeyboard getOtherKeyboard() {
        var no = new KeyboardButton(MessageUtil.getMessage("doc.other.no.button"));
        return ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow(List.of(no)))
                .resizeKeyboard(true)
                .isPersistent(false)
                .oneTimeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getKeyboardByEnumValues(GeneralType[] enums) {
        var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        for (GeneralType type: enums) {
            keyboard.add(List.of(
                    button(MessageUtil.getMessage(type.getTitleKeyword()), type.name())
            ));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static ReplyKeyboard getAddressKeyboard() {
        var button = KeyboardButton.builder()
                .text(MessageUtil.getMessage("doc.address.button"))
                .requestLocation(true)
                .build();

        return ReplyKeyboardMarkup
                .builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .isPersistent(false)
                .keyboardRow(new KeyboardRow(List.of(button)))
                .build();
    }

}
