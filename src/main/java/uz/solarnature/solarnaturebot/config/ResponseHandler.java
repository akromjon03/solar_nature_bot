package uz.solarnature.solarnaturebot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.solarnature.solarnaturebot.bot.QuestionaireBot;
import uz.solarnature.solarnaturebot.domain.entity.User;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.domain.enumeration.UserState;
import uz.solarnature.solarnaturebot.repository.UserRepository;
import uz.solarnature.solarnaturebot.service.UserService;
import uz.solarnature.solarnaturebot.utils.KeyboardFactory;
import uz.solarnature.solarnaturebot.utils.MessageUtil;

import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;
    private final UserRepository userRepository;
    private final UserService userService;

    public ResponseHandler(QuestionaireBot questionaireBot,
                           UserRepository userRepository,
                           UserService userService) {
        this.sender = questionaireBot.silent();
        this.chatStates = questionaireBot.db().getMap(Constants.CHAT_STATES);
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void replyToMessage(Message message) {
        var chatId = message.getChatId();
        var state = chatStates.get(chatId);
        var user = getUser(message.getFrom());
        if (message.hasText()) {
            var text = message.getText();

            switch (text) {
                case "/start" -> sendLanguageRequest(chatId);
            }

            if (state.equals(UserState.PHONE)) {
                user.setPhone(text);
                userRepository.save(user);
                chatStates.put(chatId, UserState.MENU);
                sendMenu(chatId);
            }

            if (state.equals(UserState.NAME)) {
                user.setFullName(text);
                userRepository.save(user);
                chatStates.put(chatId, UserState.MAIL);
                mailRequest(chatId);
            }

            if(state.equals(UserState.MAIL)){
                user.setEmail(text);
                userRepository.save(user);
                chatStates.put(chatId, UserState.ADDRESS);
                addressRequest(chatId);
            }

            if(state.equals(UserState.ADDRESS)){
                user.setAddress(text);
                userRepository.save(user);
                chatStates.put(chatId, UserState.NEXT);

            }




        }


        if (message.hasContact() && state.equals(UserState.PHONE)) {
            user.setPhone(message.getContact().getPhoneNumber());
            userRepository.save(user);
            chatStates.put(chatId, UserState.MENU);
            sendMenu(chatId);
        }

    }

    private void addressRequest(Long chatId) {
        sentTextMessage(chatId, "Enter address:");

    }

    private void mailRequest(Long chatId) {
        sentTextMessage(chatId, "Enter mail: ");
    }

    private void sendMenu(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("Choose menu")
                .replyMarkup(KeyboardFactory.getMenuKeyboard())
                .build();
        sender.execute(message);

    }

    public void replyToCallBackQuery(CallbackQuery callbackQuery) {
        var user = getUser(callbackQuery.getFrom());
        var chatId = callbackQuery.getFrom().getId();
        var data = callbackQuery.getData();


        switch (chatStates.get(chatId)) {
            case CHOOSE_LANGUAGE -> {
                user.setLanguage(UserLanguage.valueOf(data));
                userRepository.save(user);
                sendPhoneRequest(chatId);
            }

            case MENU -> {
                switch (data) {
                    case "menu1" -> {
                        sentTextMessage(chatId, "Enter full name");
                        chatStates.put(chatId, UserState.NAME);
                    }
                    case "menu2" -> menu2(chatId);
                    case "menu3" -> menu3(chatId);
                }
            }

        }

    }

    private void sentTextMessage(Long chatId, String text) {
        var message = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();

        sender.execute(message);
    }

    private void menu2(Long chatId) {

    }

    private void menu3(Long chatId) {
    }

    public User getUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        var optional = userRepository.findByChatId(tgUser.getId());
        var user = optional.orElseGet(() -> userService.createFromTgUser(tgUser));
        LocaleContextHolder.setLocale(user.getLanguage().getLocale());
        return user;
    }

    public void sendLanguageRequest(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text("Choose language!")
                .replyMarkup(KeyboardFactory.getLanguageKeyboard())
                .build();

        sender.execute(message);
        chatStates.put(chatId, UserState.CHOOSE_LANGUAGE);
    }

    public void sendPhoneRequest(Long chatId) {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(MessageUtil.getMessage("enter.phone"))
                .replyMarkup(KeyboardFactory.getPhoneKeyboard())
                .build();

        sender.execute(message);
        chatStates.put(chatId, UserState.PHONE);

    }
}

