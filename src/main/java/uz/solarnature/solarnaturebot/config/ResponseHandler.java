package uz.solarnature.solarnaturebot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.solarnature.solarnaturebot.bot.QuestionaireBot;
import uz.solarnature.solarnaturebot.domain.UserData;
import uz.solarnature.solarnaturebot.domain.entity.Document;
import uz.solarnature.solarnaturebot.domain.entity.Feedback;
import uz.solarnature.solarnaturebot.domain.entity.User;
import uz.solarnature.solarnaturebot.domain.enumeration.types.*;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;
import uz.solarnature.solarnaturebot.domain.enumeration.UserState;
import uz.solarnature.solarnaturebot.domain.enumeration.types.BuildingType;
import uz.solarnature.solarnaturebot.domain.enumeration.types.StationType;
import uz.solarnature.solarnaturebot.repository.DocumentRepository;
import uz.solarnature.solarnaturebot.repository.FeedbackRepository;
import uz.solarnature.solarnaturebot.repository.UserRepository;
import uz.solarnature.solarnaturebot.service.FileService;
import uz.solarnature.solarnaturebot.service.UserService;
import uz.solarnature.solarnaturebot.utils.AddressUtil;
import uz.solarnature.solarnaturebot.utils.KeyboardFactory;
import uz.solarnature.solarnaturebot.utils.MessageUtil;
import uz.solarnature.solarnaturebot.utils.TgMethodUtil;

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
    private final FeedbackRepository feedbackRepository;
    private final AddressUtil addressUtil;
    private final FileService fileService;
    private final QuestionaireBot bot;

    public ResponseHandler(QuestionaireBot questionaireBot,
                           UserRepository userRepository,
                           UserService userService, DocumentRepository documentRepository, FeedbackRepository feedbackRepository) {
        this.sender = questionaireBot.silent();
        this.chatStates = questionaireBot.db().getMap(Constants.CHAT_STATES);
        this.userRepository = userRepository;
        this.userService = userService;
        this.documentRepository = documentRepository;
        this.addressUtil = addressUtil;
        this.fileService = fileService;
        this.feedbackRepository = feedbackRepository;
    }

    public void replyToMessage(Message message) {
        var chatId = message.getChatId();
        var userData = getUserData(chatId);
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
                case NOT_STARTED -> {
                    sendTextWithKeyboard(chatId, "choose.language", KeyboardFactory.getLanguageKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.CHOOSE_LANGUAGE));
                }

                case MENU -> {
                    if (text.equals(MessageUtil.getMessage("menu.create"))) {
                        var doc = new Document();
                        doc.setUser(user);
                        documentRepository.save(doc);
                        sendTextWithKeyboard(chatId, "doc.type", KeyboardFactory.getKeyboardByEnumValues(DocumentType.values()));
                        chatStates.put(chatId, UserData.of(UserState.DOCUMENT_TYPE, doc.getId()));
                    }

                    if (text.equals(MessageUtil.getMessage("menu.about"))) {
                        sendText(chatId, "about.text");
                    }

                    if (text.equals(MessageUtil.getMessage("menu.feedback"))) {
                        sendText(chatId, "feedback.request");
                        chatStates.put(chatId, UserData.of(UserState.FEEDBACK));
                    }

                    if (text.equals(MessageUtil.getMessage("menu.lang"))) {
                        sendTextWithKeyboard(chatId, "choose.language", KeyboardFactory.getLanguageKeyboard());
                        chatStates.put(chatId, UserData.of(UserState.CHOOSE_LANGUAGE));
                    }

                }

                case FEEDBACK -> {
                    var feedback = new Feedback();
                    feedback.setUser(user);
                    feedback.setText(text);
                    feedbackRepository.save(feedback);
                    sendText(chatId, "feedback.response");

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
                    sendTextWithKeyboard(chatId, "doc.address", KeyboardFactory.getAddressKeyboard());
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
                            sendTextWithKeyboard(chatId, "doc.station.type", KeyboardFactory.getKeyboardByEnumValues(StationType.values()));
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

                    sendTextWithKeyboard(chatId, "doc.payment.form", KeyboardFactory.getKeyboardByEnumValues(PaymentForm.values()));
                    chatStates.put(chatId, UserData.of(UserState.PAYMENT_FORM, doc.getId()));
                }

                case SES_POWER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setSesPower(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.tool.type.panel", KeyboardFactory.getKeyboardByEnumValues(PanelBrands.values()));
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_PANEL, doc.getId()));
                }

                case TOOL_TYPE_PANEL_OTHER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setPanelOther(text);
                    documentRepository.save(doc);
                    sendTextWithKeyboard(chatId, "doc.tool.type.inverter", KeyboardFactory.getKeyboardByEnumValues(InverterBrands.values()));
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_INVERTER, doc.getId()));
                }

                case TOOL_TYPE_INVERTER_OTHER -> {
                    var doc = documentRepository.findOne(userData.getDocId());
                    doc.setInverterOther(text);
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

        if (message.hasLocation() && userData.getState().equals(UserState.ADDRESS)) {
            var doc = documentRepository.findOne(userData.getDocId());
            doc.setAddress(addressUtil.getByCoordinates(
                    message.getLocation().getLatitude(),
                    message.getLocation().getLongitude()
            ));
            documentRepository.save(doc);
            sendTextWithKeyboard(chatId, "doc.others", KeyboardFactory.getOtherKeyboard());
            chatStates.put(chatId, UserData.of(UserState.OTHERS, doc.getId()));
        }

    }

    public void replyToCallBackQuery(CallbackQuery callbackQuery) {
        var user = getUser(callbackQuery.getFrom());
        var chatId = callbackQuery.getFrom().getId();
        var userData = getUserData(chatId);
        var data = callbackQuery.getData();

        switch (userData.getState()) {
            case CHOOSE_LANGUAGE -> {
                user.setLanguage(UserLanguage.valueOf(data));
                userRepository.save(user);
                LocaleContextHolder.setLocale(user.getLanguage().getLocale());
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

                sendTextWithKeyboard(chatId, "doc.filling.type", KeyboardFactory.getKeyboardByEnumValues(FillingType.values()));
                chatStates.put(chatId, UserData.of(UserState.FILLING_TYPE, doc.getId()));
            }

            case FILLING_TYPE -> {
                var fillingType = FillingType.valueOf(data);
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setFillingType(fillingType);
                documentRepository.save(doc);

                if (FillingType.ONLINE.equals(fillingType)) {
                    if (doc.isBusiness()) {
                        sendText(chatId, "doc.company.name");
                        chatStates.put(chatId, UserData.of(UserState.COMPANY_NAME, doc.getId()));
                    } else {
                        sendText(chatId, "doc.name");
                        chatStates.put(chatId, UserData.of(UserState.NAME, doc.getId()));
                    }
                } else {
                    sendFile(chatId, doc);
                    sendTextWithKeyboard(chatId, "doc.final.text", KeyboardFactory.getMenuKeyboard());
                    chatStates.put(chatId, UserData.of(UserState.MENU));
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

            case TOOL_TYPE_PANEL -> {
                var panel = PanelBrands.valueOf(data);
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setPanel(panel);
                documentRepository.save(doc);

                if (PanelBrands.OTHER.equals(panel)) {
                    sendText(chatId, "doc.tool.type.panel.other");
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_PANEL_OTHER, doc.getId()));
                } else {
                    sendTextWithKeyboard(chatId, "doc.tool.type.inverter", KeyboardFactory.getKeyboardByEnumValues(InverterBrands.values()));
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_INVERTER, doc.getId()));
                }
            }

            case TOOL_TYPE_INVERTER -> {
                var inverter = InverterBrands.valueOf(data);
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setInverter(inverter);
                documentRepository.save(doc);

                if (InverterBrands.OTHER.equals(inverter)) {
                    sendText(chatId, "doc.tool.type.inverter.other");
                    chatStates.put(chatId, UserData.of(UserState.TOOL_TYPE_INVERTER_OTHER, doc.getId()));
                } else {
                    sendText(chatId, "doc.visit.date");
                    chatStates.put(chatId, UserData.of(UserState.VISIT_DATE, doc.getId()));
                }
            }

            case PAYMENT_FORM -> {
                var doc = documentRepository.findOne(userData.getDocId());
                doc.setPaymentForm(PaymentForm.valueOf(data));
                documentRepository.save(doc);
                sendText(chatId, "doc.visit.date");
                chatStates.put(chatId, UserData.of(UserState.VISIT_DATE, doc.getId()));
            }

        }

    }

    private UserData getUserData(Long chatId) {
        if (chatStates.containsKey(chatId)) {
            return chatStates.get(chatId);
        } else {
            var data = UserData.defaultState();
            chatStates.put(chatId, data);
            return data;
        }
    }

    private void sendFile(Long chatId, Document doc) {
        var file = fileService.getFileByDocument(doc);
        var fileName = MessageUtil.getMessage(doc.getDocumentType().getTitleKeyword()).concat(".pdf");
        var sendDocument = TgMethodUtil.document(chatId, file, fileName);
        try {
            bot.execute(sendDocument);
        } catch (TelegramApiException e) {
            sendTextWithKeyboard(chatId, "error", KeyboardFactory.getMenuKeyboard());
            chatStates.put(chatId, UserData.of(UserState.MENU));
        }
    }

    private void sendBuildingTypeOrSesPowerRequest(Long chatId, Document doc) {
        if (doc.getDocumentType().equals(DocumentType.SOLAR_PANEL)) {
            sendTextWithKeyboard(chatId, "doc.building.type", KeyboardFactory.getKeyboardByEnumValues(BuildingType.values()));
            chatStates.put(chatId, UserData.of(UserState.BUILDING_TYPE, doc.getId()));
        } else {
            sendText(chatId, "doc.ses.power");
            chatStates.put(chatId, UserData.of(UserState.SES_POWER, doc.getId()));
        }
    }

    private void sendText(Long chatId, String text) {
        var message = TgMethodUtil.textMessage(chatId, text);
        sender.execute(message);
    }

    private void sendTextWithKeyboard(Long chatId, String text, ReplyKeyboard keyboard) {
        var message = TgMethodUtil.textMessageWithKeyboard(chatId, text, keyboard);
        sender.execute(message);
    }

    public User getUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        var optional = userRepository.findByChatId(tgUser.getId());
        var user = optional.orElseGet(() -> userService.createFromTgUser(tgUser));
        LocaleContextHolder.setLocale(user.getLanguage().getLocale());
        return user;
    }

}

