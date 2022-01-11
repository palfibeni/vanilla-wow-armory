package com.palfib.vanilla.wow.armory.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.palfib.vanilla.wow.armory.data.enums.WowHeadResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WowheadSuggestionDTO {
    private Integer type;
    private Integer id;
    private String name;
    private WowHeadResultType typeName;
    private String icon;
    private Integer quality;
}
