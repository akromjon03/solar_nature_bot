package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FillingType implements GeneralType {

    ONLINE("doc.filling.type.online.title"),
    ON_PAPER("doc.filling.type.onpaper.title");

    private final String titleKeyword;

}
