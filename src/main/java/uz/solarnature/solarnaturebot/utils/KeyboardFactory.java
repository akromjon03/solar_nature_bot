package uz.solarnature.solarnaturebot.utils;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.solarnature.solarnaturebot.domain.enumeration.DocumentType;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.domain.enumeration.types.BuildingType;
import uz.solarnature.solarnaturebot.domain.enumeration.types.StationType;

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
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getMenuKeyboard() {
        var create = new KeyboardButton(MessageUtil.getMessage("menu.create"));
        var about = new KeyboardButton(MessageUtil.getMessage("menu.about"));
        var feedback = new KeyboardButton(MessageUtil.getMessage("menu.feedback"));

        var keyboard = List.of(
                new KeyboardRow(List.of(create)),
                new KeyboardRow(List.of(about)),
                new KeyboardRow(List.of(feedback))
        );


        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getDocTypeKeyboard() {
        var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        for (DocumentType type: DocumentType.values()) {
            keyboard.add(List.of(
                    button(MessageUtil.getMessage(type.getTitleKeyword()), type.name())
            ));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
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
                .oneTimeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard getStationTypeKeyboard() {
        var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        for (StationType type: StationType.values()) {
            keyboard.add(List.of(
                    button(MessageUtil.getMessage(type.getTitleKeyword()), type.name())
            ));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static ReplyKeyboard getBuildingTypeKeyboard() {
        var keyboard = new ArrayList<List<InlineKeyboardButton>>();

        for (BuildingType type: BuildingType.values()) {
            keyboard.add(List.of(
                    button(MessageUtil.getMessage(type.getTitleKeyword()), type.name())
            ));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
