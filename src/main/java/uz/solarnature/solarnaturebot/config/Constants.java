package uz.solarnature.solarnaturebot.config;

import java.time.format.DateTimeFormatter;

public interface Constants {

    String CHAT_STATES = "questionaire_bot_states";
    String START_DESCRIPTION = "Start description";

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

}
