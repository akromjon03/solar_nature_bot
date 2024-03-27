package uz.solarnature.solarnaturebot.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import uz.solarnature.solarnaturebot.config.properties.ApplicationProperties;
import uz.solarnature.solarnaturebot.domain.entity.Document;

import java.io.File;
import java.nio.file.Paths;

@Service
public class FileService {

    private final String CHECKLIST_FOLDER;

    public FileService(ApplicationProperties properties) {
        CHECKLIST_FOLDER = properties.getChecklistFolder();
    }

    public File getFileByDocument(Document doc) {
        var lang = LocaleContextHolder.getLocale().getLanguage();
        var accountFolder = doc.isBusiness() ? "commercial" : "private";
        var fileName = doc.getDocumentType().getFileName();
        var filePath = StringUtils.joinWith("/", CHECKLIST_FOLDER, accountFolder, lang, fileName);
        return Paths.get(filePath).toFile();
    }

}
