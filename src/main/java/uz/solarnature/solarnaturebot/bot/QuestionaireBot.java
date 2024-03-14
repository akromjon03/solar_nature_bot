package uz.solarnature.solarnaturebot.bot;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.solarnature.solarnaturebot.config.Constants;
import uz.solarnature.solarnaturebot.config.ResponseHandler;
import uz.solarnature.solarnaturebot.config.properties.ApplicationProperties;

import java.util.function.BiConsumer;

@Component
public class QuestionaireBot extends AbilityBot {

    private final ResponseHandler responseHandler;

    public QuestionaireBot(ApplicationProperties properties) {
        super(
                properties.getBot().getToken(),
                properties.getBot().getUsername()
        );
        responseHandler = new ResponseHandler(silent, db);
    }

    @Override
    public long creatorId() {
        return 1L;
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Reply replyToMessage() {
        return Reply.of(
                ((baseAbilityBot, update) -> responseHandler.replyToMessage(update.getMessage())),
                Flag.MESSAGE
        );
    }

    public Reply replyToCallBack() {
        return Reply.of(
                ((baseAbilityBot, update) -> responseHandler.replyToCallBackQuery(update.getCallbackQuery())),
                Flag.CALLBACK_QUERY
        );
    }

}
