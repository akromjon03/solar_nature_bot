package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType implements GeneralType {

    SOLAR_PANEL(
            "doc.type.panel.title",
            "panel.description",
            "pv.pdf"
    ),

    WIND(
            "doc.type.wind.title",
            "wind.description",
            "wind.pdf"
    ),

    FULL_SERVICE(
            "doc.type.fullservice.title",
            "fullservice.description",
            "service.pdf"
    );

    private final String titleKeyword;
    private final String descriptionKeyword;
    private final String fileName;

}
