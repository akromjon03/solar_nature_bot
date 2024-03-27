package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InverterBrands implements GeneralType {

    SUNGROW("doc.tool.type.inverter.sungrow.title"),
    SOLAX("doc.tool.type.inverter.solax.title"),
    MUST("doc.tool.type.inverter.must.title"),
    HUAWEI("doc.tool.type.inverter.huawei.title"),
    GROWATT("doc.tool.type.inverter.growatt.title"),
    OTHER("doc.tool.type.inverter.other.title");

    private final String titleKeyword;

}
