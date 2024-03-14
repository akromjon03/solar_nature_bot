package uz.solarnature.solarnaturebot.config;

import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.solarnature.solarnaturebot.domain.enumeration.UserState;

import java.util.Map;

@Slf4j
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
        chatStates = db.getMap(Constants.CHAT_STATES);
    }

    public void replyToMessage(Message message) {
        var chatId = message.getChatId();
        if (message.hasText()) {
            var text = message.getText();

            switch (text) {
                case "/start" -> {
                    replyToStart(chatId);
                    chatStates.put(chatId, UserState.CHOOSE_LANGUAGE);
                }
            }

        }

    }

    public void replyToCallBackQuery(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();
        var data = callbackQuery.getData();

        switch (chatStates.get(chatId)) {
            case CHOOSE_LANGUAGE -> {

            }
        }

    }

    public void replyToStart(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("Welcome to the Solar Nature Bot! Choose language.")
                .build();

        sender.execute(message);
        chatStates.put(chatId, UserState.CHOOSE_LANGUAGE);
    }
}

