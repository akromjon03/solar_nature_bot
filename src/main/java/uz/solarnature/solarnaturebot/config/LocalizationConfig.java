package uz.solarnature.solarnaturebot.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import uz.solarnature.solarnaturebot.domain.enumeration.UserLanguage;

@Configuration
public class LocalizationConfig {

    @Bean
    @Primary
    public MessageSource messageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(UserLanguage.RUSSIAN.getLocale());
        return messageSource;
    }

}
