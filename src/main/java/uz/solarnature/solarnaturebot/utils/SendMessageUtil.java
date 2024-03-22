package uz.solarnature.solarnaturebot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class SendMessageUtil {

    public static SendMessage textMessage(Long chatId, String text) {
        return SendMessage.builder()
                .text(MessageUtil.getMessage(text))
                .parseMode("HTML")
                .chatId(chatId)
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

}
