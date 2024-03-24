package uz.solarnature.solarnaturebot.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    SOLAR_PANEL("doc.type.panel.title", "panel.description"),
    WIND("doc.type.wind.title", "wind.description"),
    FULL_SERVICE("doc.type.fullservice.title", "fullservice.description");

    private final String titleKeyword;
    private final String descriptionKeyword;

}
