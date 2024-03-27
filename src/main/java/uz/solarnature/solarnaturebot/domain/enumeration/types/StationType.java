package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StationType implements GeneralType {

    ROOF("doc.station.type.roof.title"),
    GROUND("doc.station.type.ground.title"),
    FACADE("doc.station.type.facade.title"),
    OTHER("doc.station.type.other.title");

    private final String titleKeyword;

}
