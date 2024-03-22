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
import uz.solarnature.solarnaturebot.domain.entity.Company;
import uz.solarnature.solarnaturebot.domain.entity.User;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.domain.enumeration.UserState;
import uz.solarnature.solarnaturebot.repository.CompanyRepository;
import uz.solarnature.solarnaturebot.repository.UserRepository;
import uz.solarnature.solarnaturebot.service.UserService;
import uz.solarnature.solarnaturebot.utils.KeyboardFactory;
import uz.solarnature.solarnaturebot.utils.MessageUtil;

import java.util.Map;

@Slf4j
@Service
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CompanyRepository companyRepository;

    public ResponseHandler(QuestionaireBot questionaireBot,
                           UserRepository userRepository,
                           UserService userService, CompanyRepository companyRepository) {
        this.sender = questionaireBot.silent();
        this.chatStates = questionaireBot.db().getMap(Constants.CHAT_STATES);
        this.userRepository = userRepository;
        this.userService = userService;
        this.companyRepository = companyRepository;
    }

    public void replyToMessage(Message message) {
        var chatId = message.getChatId();
        var state = chatStates.get(chatId);
        var user = getUser(message.getFrom());
        var company = new Company();
        company.setChatId(chatId);

        if (message.hasText()) {
            var text = message.getText();

            switch (text) {
                case "/start" -> sendLanguageRequest(chatId);
            }

            if (state.equals(UserState.PHONE)) {
                user.setPhone(text);
                userRepository.save(user);
                chatStates.put(chatId, UserState.NAME);
                nameRequest(chatId);

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
                chatStates.put(chatId, UserState.COMPANY_NAME);
                companyNameRequest(chatId);
            }

            if (state.equals(UserState.COMPANY_NAME)){
                company.setCompanyName(text);
//                companyRepository.save(company);
                chatStates.put(chatId, UserState.TIN);
                companyTINRequest(chatId);
            }

            if (state.equals(UserState.TIN)){
                company.setCompanyName(text);
//                companyRepository.save(company);
                chatStates.put(chatId, UserState.CONTACT_PERSON);
                companyPersonRequest(chatId);
            }

            if (state.equals(UserState.CONTACT_PERSON)){
                company.setCompanyName(text);
//                companyRepository.save(company);
                chatStates.put(chatId, UserState.COMPANY_PHONE_NUMBER);
                companyPhoneNumberRequest(chatId);
            }

            if (state.equals(UserState.COMPANY_PHONE_NUMBER)){
                company.setCompanyName(text);
//                companyRepository.save(company);
                chatStates.put(chatId, UserState.COMPANY_MAIL);
                companyMailRequest(chatId);
            }

            if (state.equals(UserState.COMPANY_MAIL)){
                company.setCompanyName(text);
                companyRepository.save(company);
                chatStates.put(chatId, UserState.NEXT);
                companyAddressRequest(chatId);
            }







        }


        if (message.hasContact() && state.equals(UserState.PHONE)) {
            user.setPhone(message.getContact().getPhoneNumber());
            userRepository.save(user);
            chatStates.put(chatId, UserState.NAME);
            nameRequest(chatId);
        }

    }

    private void companyAddressRequest(Long chatId) {
        sentTextMessage(chatId,"company.address");
    }

    private void companyMailRequest(Long chatId) {
        sentTextMessage(chatId, "company.mail");
    }

    private void companyPhoneNumberRequest(Long chatId) {
        sentTextMessage(chatId, "company.phone.number");
    }

    private void companyPersonRequest(Long chatId) {
        sentTextMessage(chatId, "company.person");
    }

    private void companyTINRequest(Long chatId) {
        sentTextMessage(chatId, "company.TIN" );
    }

    private void companyNameRequest(Long chatId) {
        sentTextMessage(chatId, "company.name");
    }



    private void nameRequest(Long chatId) {
        sentTextMessage(chatId, "user.name");
    }

    private void addressRequest(Long chatId) {
        sentTextMessage(chatId, "user.address");

    }

    private void mailRequest(Long chatId) {
        sentTextMessage(chatId, "user.mail");
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
        }

    }

    private void sentTextMessage(Long chatId, String text) {
        var message = SendMessage.builder()
                .text(MessageUtil.getMessage(text))
                .replyMarkup(new ReplyKeyboardRemove(true))
                .chatId(chatId)
                .build();

        sender.execute(message);
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

