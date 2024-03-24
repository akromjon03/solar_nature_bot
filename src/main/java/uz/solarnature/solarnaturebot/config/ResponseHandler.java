package uz.solarnature.solarnaturebot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.solarnature.solarnaturebot.bot.QuestionaireBot;
import uz.solarnature.solarnaturebot.domain.UserData;
import uz.solarnature.solarnaturebot.domain.entity.Document;
import uz.solarnature.solarnaturebot.domain.entity.User;
import uz.solarnature.solarnaturebot.domain.enumeration.DocumentType;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.domain.enumeration.UserState;
import uz.solarnature.solarnaturebot.repository.DocumentRepository;
import uz.solarnature.solarnaturebot.repository.UserRepository;
import uz.solarnature.solarnaturebot.service.UserService;
import uz.solarnature.solarnaturebot.utils.KeyboardFactory;
import uz.solarnature.solarnaturebot.utils.MessageUtil;
import uz.solarnature.solarnaturebot.utils.SendMessageUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserData> chatStates;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DocumentRepository documentRepository;

    public ResponseHandler(QuestionaireBot questionaireBot,
                           UserRepository userRepository,
                           UserService userService, DocumentRepository documentRepository) {
        this.sender = questionaireBot.silent();
        this.chatStates = questionaireBot.db().getMap(Constants.CHAT_STATES);
        this.userRepository = userRepository;
        this.userService = userService;
        this.documentRepository = documentRepository;
    }

    public void replyToMessage(Message message) {
        var chatId = message.getChatId();
        var userData = chatStates.get(chatId);
        var user = getUser(message.getFrom());

        if (message.hasText()) {
            var text = message.getText();

            switch (text) {
                case "/start" -> {

                    sendTextWithKeyboard(chatId, "choose.language", KeyboardFactory.getLanguageKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.CHOOSE_LANGUAGE));
                }
            }

            switch (userData.getState()) {
                case MENU -> {
                    if (text.equals(MessageUtil.getMessage("menu.create"))) {
                        var doc = new Document();
                        documentRepository.save(doc);
                        sendTextWithKeyboard(chatId, "doc.type", KeyboardFactory.getDocTypeKeyboard());
                        chatStates.put(chatId, UserData.of(UserState.DOCUMENT_TYPE, doc.getId()));
                    }

                    if (text.equals(MessageUtil.getMessage("menu.about"))) {

                    }

                    if (text.equals(MessageUtil.getMessage("menu.feedback"))) {

                    }
                }

                case NAME -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setFullName(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.phone", KeyboardFactory.getPhoneKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.PHONE, doc.getId()));
                }

                case COMPANY_NAME -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setCompanyName(text);
                    documentRepository.save(doc);
                    sendTextMessage(chatId, "doc.company.tin");
                    chatStates.put(chatId, UserData.of(UserState.COMPANY_TIN, doc.getId()));
                }

                case COMPANY_TIN -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setTin(text);
                    documentRepository.save(doc);
                    sendTextMessage(chatId, "doc.contact.name");
                    chatStates.put(chatId, UserData.of(UserState.MENU, doc.getId()));
                }

                case PHONE -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setPhone(text);
                    documentRepository.save(doc);
                    sendTextMessage(chatId, "doc.email");
                    userData.setState(UserState.EMAIL);
                    chatStates.put(chatId, UserData.of(UserState.EMAIL, doc.getId()));
                }

                case EMAIL -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setEmail(text);
                    documentRepository.save(doc);
                    sendTextMessage(chatId, "doc.address");
                    chatStates.put(chatId, UserData.of(UserState.ADDRESS, doc.getId()));
                }

                case ADDRESS -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setAddress(text);
                    documentRepository.save(doc);
                    sendTextMessage(chatId, "doc.others");
                    chatStates.put(chatId, UserData.of(UserState.OTHERS, doc.getId()));
                }

                case OTHERS -> {
                    var doc = documentRepository.getById(userData.getDocId());
                    doc.setOthers(text);
                    documentRepository.save(doc);
                }
            }

        }

        if (message.hasContact() && userData.getState().equals(UserState.PHONE)) {
            var doc = documentRepository.getById(userData.getDocId());
            doc.setPhone(message.getContact().getPhoneNumber());
            documentRepository.save(doc);
            sendTextMessage(chatId, "doc.email");
//            userData.setState(UserState.EMAIL);
            chatStates.put(chatId, UserData.of(UserState.EMAIL));
        }

    }

    public void replyToCallBackQuery(CallbackQuery callbackQuery) {
        var user = getUser(callbackQuery.getFrom());
        var chatId = callbackQuery.getFrom().getId();
        var userData = chatStates.get(chatId);
        var data = callbackQuery.getData();

        switch (userData.getState()) {
            case CHOOSE_LANGUAGE -> {
                user.setLanguage(UserLanguage.valueOf(data));
                userRepository.save(user);
                sendTextWithKeyboard(chatId, "choose.menu", KeyboardFactory.getMenuKeyboard());
                chatStates.put(chatId, UserData.of(UserState.MENU));
            }

            case DOCUMENT_TYPE -> {
                var doc = documentRepository.getById(userData.getDocId());
                doc.setDocumentType(DocumentType.valueOf(data));
                documentRepository.save(doc);
                sendTextWithKeyboard(chatId, "doc.account.type", KeyboardFactory.getAccountKeyboard());
                chatStates.put(chatId, UserData.of(UserState.ACCOUNT_TYPE));
            }

            case ACCOUNT_TYPE -> {
                var doc = documentRepository.getById(userData.getDocId());
                doc.setBusiness(data.equals("commercial"));
                documentRepository.save(doc);

                if (doc.isBusiness()) {
                    sendTextMessage(chatId, "doc.company.name");
                    chatStates.put(chatId, UserData.of(UserState.COMPANY_NAME));
                } else {
                    sendTextMessage(chatId, "doc.name");
                    chatStates.put(chatId, UserData.of(UserState.NAME));
                }
            }
            default -> {
                System.out.println("_________asdasd__________");
            }
        }

    }

    private void sendTextMessage(Long chatId, String text) {
        var message = SendMessageUtil.textMessage(chatId, text);
        sender.execute(message);
    }

    private void sendTextWithKeyboard(Long chatId, String text, ReplyKeyboard keyboard) {
        var message = SendMessageUtil.textMessageWithKeyboard(chatId, text, keyboard);
        sender.execute(message);
    }

    public User getUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        var optional = userRepository.findByChatId(tgUser.getId());
        var user = optional.orElseGet(() -> userService.createFromTgUser(tgUser));
        LocaleContextHolder.setLocale(user.getLanguage().getLocale());
        return user;
    }

}

