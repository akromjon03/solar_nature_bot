package uz.solarnature.solarnaturebot.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    SOLAR_PANEL("panel.title", "panel.description"),
    WIND("wind.title", "wind.description"),
    FULL_SERVICE("fullservice.title", "fullservice.description");

    private final String descriptionKeyword;
    private final String titleKeyword;

}
