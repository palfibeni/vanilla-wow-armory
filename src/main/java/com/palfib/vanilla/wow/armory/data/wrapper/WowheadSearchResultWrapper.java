package com.palfib.vanilla.wow.armory.data.wrapper;

import com.palfib.vanilla.wow.armory.data.enums.WowHeadResultType;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSuggestionDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

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
    private final List<BossDetailWrapper> bossDetailWrappers;

    @Builder
    public WowheadSearchResultWrapper(final WowheadSuggestionDTO wowheadSuggestionDTO, final String iconUrl, final String details, final List<BossDetailWrapper> bossDetailWrappers) {
        this.type = Optional.ofNullable(wowheadSuggestionDTO).map(WowheadSuggestionDTO::getType).orElse(null);
        this.id = Optional.ofNullable(wowheadSuggestionDTO).map(WowheadSuggestionDTO::getId).orElse(null);
        this.name = Optional.ofNullable(wowheadSuggestionDTO).map(WowheadSuggestionDTO::getName).orElse(null);
        this.typeName = Optional.ofNullable(wowheadSuggestionDTO).map(WowheadSuggestionDTO::getTypeName).map(WowHeadResultType::toString).orElse(null);
        this.icon = Optional.ofNullable(wowheadSuggestionDTO).map(WowheadSuggestionDTO::getIcon).orElse(null);
        this.quality = Optional.ofNullable(wowheadSuggestionDTO).map(WowheadSuggestionDTO::getQuality).orElse(null);
        this.iconUrl = iconUrl;
        this.details = details;
        this.bossDetailWrappers = bossDetailWrappers;
    }
}
