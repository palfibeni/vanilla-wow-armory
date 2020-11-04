package com.palfib.vanilla.wow.armory.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String typeName;
    private String icon;
    private Integer quality;
}
