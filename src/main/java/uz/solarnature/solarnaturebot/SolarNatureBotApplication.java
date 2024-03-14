package uz.solarnature.solarnaturebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.solarnature.solarnaturebot.config.properties.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class SolarNatureBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolarNatureBotApplication.class, args);
	}

}
