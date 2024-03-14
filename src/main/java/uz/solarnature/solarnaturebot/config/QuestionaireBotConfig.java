package uz.solarnature.solarnaturebot.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.solarnature.solarnaturebot.bot.QuestionaireBot;

@Configuration
@RequiredArgsConstructor
public class QuestionaireBotConfig {

    private final QuestionaireBot questionaireBot;

    @PostConstruct
    public void initBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(questionaireBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
