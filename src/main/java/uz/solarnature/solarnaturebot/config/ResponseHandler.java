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
import uz.solarnature.solarnaturebot.domain.enumeration.types.BuildingType;
import uz.solarnature.solarnaturebot.domain.enumeration.types.StationType;
import uz.solarnature.solarnaturebot.repository.DocumentRepository;
import uz.solarnature.solarnaturebot.repository.UserRepository;
import uz.solarnature.solarnaturebot.service.UserService;
import uz.solarnature.solarnaturebot.utils.KeyboardFactory;
import uz.solarnature.solarnaturebot.utils.MessageUtil;
import uz.solarnature.solarnaturebot.utils.SendMessageUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                        doc.setUser(user);
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
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setFullName(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.phone", KeyboardFactory.getPhoneKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.PHONE, doc.getId()));
                }

                case COMPANY_NAME -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setCompanyName(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.company.tin");
                    chatStates.put(chatId, UserData.of(UserState.COMPANY_TIN, doc.getId()));
                }

                case COMPANY_TIN -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setTin(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.contact.name");
                    chatStates.put(chatId, UserData.of(UserState.NAME, doc.getId()));
                }

                case PHONE -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setPhone(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.email");
                    userData.setState(UserState.EMAIL);
                    chatStates.put(chatId, UserData.of(UserState.EMAIL, doc.getId()));
                }

                case EMAIL -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setEmail(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.address");
                    chatStates.put(chatId, UserData.of(UserState.ADDRESS, doc.getId()));
                }

                case ADDRESS -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setAddress(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.others", KeyboardFactory.getOtherKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.OTHERS, doc.getId()));
                }

                case OTHERS -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setOthers(text);
                    documentRepository.save(doc);

                    switch (doc.getDocumentType()) {
                        case SOLAR_PANEL, FULL_SERVICE -> {
                            sendTextWithKeyboard(chatId, "doc.station.type", KeyboardFactory.getStationTypeKeyboard());
                            chatStates.put(chatId, UserData.of(UserState.STATION_TYPE, doc.getId()));
                        }

                        case WIND -> {
                            sendText(chatId, "doc.visit.date");
                            chatStates.put(chatId, UserData.of(UserState.VISIT_DATE, doc.getId()));
                        }
                    }

                }

                case STATION_TYPE_OTHER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setStationTypeOther(text);
                    documentRepository.save(doc);
                    sendBuildingTypeOrSesPowerRequest(chatId, doc);
                }

                case BUILDING_TYPE_OTHER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setBuildingTypeOther(text);
                    documentRepository.save(doc);

                    sendText(chatId, "doc.plan");
                    chatStates.put(chatId, UserData.of(UserState.PLAN, doc.getId()));
                }

                case PLAN -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setPlan(text);
                    documentRepository.save(doc);

                    sendText(chatId, "doc.payment.form");
                    chatStates.put(chatId, UserData.of(UserState.PAYMENT_FORM, doc.getId()));
                }

                case PAYMENT_FORM -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setPaymentForm(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.visit.date");
                    chatStates.put(chatId, UserData.of(UserState.VISIT_DATE, doc.getId()));
                }

                case SES_POWER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setSesPower(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.tool.type.panel");
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_PANEL, doc.getId()));
                }

                case TOOL_TYPE_PANEL -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setPanel(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.tool.type.inverter");
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_INVERTER, doc.getId()));
                }

                case TOOL_TYPE_INVERTER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setInverter(text);
                    documentRepository.save(doc);
                    sendText(chatId, "doc.visit.date");
                    chatStates.put(chatId, UserData.of(UserState.VISIT_DATE, doc.getId()));
                }

                case VISIT_DATE -> {
                    try {
                        var visitDate = LocalDate.parse(text, Constants.DATE_FORMATTER);
                        var doc = documentRepository.findOne(userData.getDocId());
                        doc.setVisitDate(visitDate);
                        documentRepository.save(doc);
                        sendText(chatId, "doc.contact.time");
                        chatStates.put(chatId, UserData.of(UserState.CONTACT_TIME, doc.getId()));
                    } catch (RuntimeException e) {
                        sendText(chatId, "doc.visit.date.format");
                    }
                }

                case CONTACT_TIME -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setContactTime(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.commercial.offer", KeyboardFactory.getOtherKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.COMMERCIAL_OFFER, doc.getId()));
                }

                case COMMERCIAL_OFFER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setCommercialOffer(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.final.text", KeyboardFactory.getMenuKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.MENU));
                }

            }

        }

        if (message.hasContact() && userData.getState().equals(UserState.PHONE)) {
            var doc = documentRepository.findOne(userData.getDocId());
            doc.setPhone(message.getContact().getPhoneNumber());
            documentRepository.save(doc);
            sendText(chatId, "doc.email");
            chatStates.put(chatId, UserData.of(UserState.EMAIL, doc.getId()));
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
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setDocumentType(DocumentType.valueOf(data));
                documentRepository.save(doc);
                sendTextWithKeyboard(chatId, "doc.account.type", KeyboardFactory.getAccountKeyboard());
                chatStates.put(chatId, UserData.of(UserState.ACCOUNT_TYPE, doc.getId()));
            }

            case ACCOUNT_TYPE -> {
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setBusiness(data.equals("commercial"));
                documentRepository.save(doc);

                if (doc.isBusiness()) {
                    sendText(chatId, "doc.company.name");
                    chatStates.put(chatId, UserData.of(UserState.COMPANY_NAME, doc.getId()));
                } else {
                    sendText(chatId, "doc.name");
                    chatStates.put(chatId, UserData.of(UserState.NAME, doc.getId()));
                }
            }

            case STATION_TYPE -> {
                var stationType = StationType.valueOf(data);
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setStationType(stationType);
                documentRepository.save(doc);

                if (StationType.OTHER.equals(stationType)) {
                    sendText(chatId, "doc.station.type.other");
                    chatStates.put(chatId, UserData.of(UserState.STATION_TYPE_OTHER, doc.getId()));
                } else {
                    sendBuildingTypeOrSesPowerRequest(chatId, doc);
                }
            }

            case BUILDING_TYPE -> {
                var buildingType = BuildingType.valueOf(data);
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setBuildingType(buildingType);
                documentRepository.save(doc);

                if (BuildingType.OTHER.equals(buildingType)) {
                    sendText(chatId, "doc.building.type.other");
                    chatStates.put(chatId, UserData.of(UserState.BUILDING_TYPE_OTHER, doc.getId()));
                } else {
                    sendText(chatId, "doc.plan");
                    chatStates.put(chatId, UserData.of(UserState.PLAN, doc.getId()));
                }
            }

        }

    }

    private void sendBuildingTypeOrSesPowerRequest(Long chatId, Document doc) {
        if (doc.getDocumentType().equals(DocumentType.SOLAR_PANEL)) {
            sendTextWithKeyboard(chatId, "doc.building.type", KeyboardFactory.getBuildingTypeKeyboard());
            chatStates.put(chatId, UserData.of(UserState.BUILDING_TYPE, doc.getId()));
        } else {
            sendText(chatId, "doc.ses.power");
            chatStates.put(chatId, UserData.of(UserState.SES_POWER, doc.getId()));
        }
    }

    private void sendText(Long chatId, String text) {
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

