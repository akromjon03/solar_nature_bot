package uz.solarnature.solarnaturebot.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private FileStorageProperties fileStorage = new FileStorageProperties();
    private BotProperties bot = new BotProperties();

    public String getFileStorageRoot() {
        return fileStorage.getRootPath();
    }
}