package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PanelBrands implements GeneralType {

    ASTRONERGY("doc.tool.type.panel.astronergy.title"),
    LONGI("doc.tool.type.panel.longi.title"),
    RESUN("doc.tool.type.panel.resun.title"),
    JINKO("doc.tool.type.panel.jinko.title"),
    YUANCHAN("doc.tool.type.panel.yuanchan.title"),
    TW("doc.tool.type.panel.tw.title"),
    OTHER("doc.tool.type.panel.other.title");

    private final String titleKeyword;
}
