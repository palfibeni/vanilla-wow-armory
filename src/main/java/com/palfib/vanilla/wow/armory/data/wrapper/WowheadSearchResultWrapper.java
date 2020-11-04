package com.palfib.vanilla.wow.armory.data.wrapper;

import com.palfib.vanilla.wow.armory.data.dto.WowheadSuggestionDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WowheadSearchResultWrapper {
    private final Integer type;
    private final Integer id;
    private final String name;
    private final String typeName;
    private final String icon;
    private final Integer quality;
    private final String iconUrl;
    private final String details;

    @Builder
    public WowheadSearchResultWrapper(final WowheadSuggestionDTO wowheadSuggestionDTO, final String iconUrl, final String details) {
        this.type = wowheadSuggestionDTO.getType();
        this.id = wowheadSuggestionDTO.getId();
        this.name = wowheadSuggestionDTO.getName();
        this.typeName = wowheadSuggestionDTO.getTypeName();
        this.icon = wowheadSuggestionDTO.getIcon();
        this.quality = wowheadSuggestionDTO.getQuality();
        this.iconUrl = iconUrl;
        this.details = details;
    }
}
