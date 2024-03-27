package uz.solarnature.solarnaturebot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;

public class TgMethodUtil {

    public static SendMessage textMessage(Long chatId, String text) {
        return SendMessage.builder()
                .text(MessageUtil.getMessage(text))
                .parseMode("HTML")
                .chatId(chatId)
                .replyMarkup(
                        ReplyKeyboardMarkup.builder()
                                .clearKeyboard()
                                .build()
                )
                .build();
    }

    public static SendMessage textMessageWithKeyboard(Long chatId, String text, ReplyKeyboard keyboard) {
        return SendMessage.builder()
                .text(MessageUtil.getMessage(text))
                .parseMode("HTML")
                .chatId(chatId)
                .replyMarkup(keyboard)
                .build();
    }

    public static SendDocument document(Long chatId, File file, String fileName) {
        var inputFile = new InputFile();
        inputFile.setMedia(file, fileName);

        return SendDocument.builder()
                .chatId(chatId)
                .document(inputFile)
                .parseMode("HTML")
                .build();
    }

}
