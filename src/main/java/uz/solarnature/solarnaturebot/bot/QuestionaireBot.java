package uz.solarnature.solarnaturebot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.*;
import uz.solarnature.solarnaturebot.config.ResponseHandler;
import uz.solarnature.solarnaturebot.config.properties.ApplicationProperties;

@Component
public class QuestionaireBot extends AbilityBot {

    @Lazy
    @Autowired
    private ResponseHandler responseHandler;

    public QuestionaireBot(ApplicationProperties properties) {
        super(
                properties.getBot().getToken(),
                properties.getBot().getUsername()
        );
    }

    @Override
    public long creatorId() {
        return 1L;
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
