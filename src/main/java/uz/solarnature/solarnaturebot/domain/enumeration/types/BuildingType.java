package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BuildingType implements GeneralType {

    COTTAGE("doc.building.type.cottage.title"),
    SHOP("doc.building.type.shop.title"),
    HOTEL("doc.building.type.hotel.title"),
    PRODUCTION("doc.building.type.production.title"),
    OFFICE("doc.building.type.office.title"),
    CAFE_RESTAURANT("doc.building.type.cafe.title"),
    SANATORIUM("doc.building.type.sanatorium.title"),
    OTHER("doc.building.type.other.title");

    private final String titleKeyword;

}
