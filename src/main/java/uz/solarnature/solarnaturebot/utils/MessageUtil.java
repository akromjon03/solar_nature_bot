package uz.solarnature.solarnaturebot.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

    public static final Object[] EMPTY_ARRAY = new Object[]{};
    private static MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String message){
        return messageSource.getMessage(message, EMPTY_ARRAY, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String message, Object... args){
        return messageSource.getMessage(message, args, LocaleContextHolder.getLocale());
    }
}
