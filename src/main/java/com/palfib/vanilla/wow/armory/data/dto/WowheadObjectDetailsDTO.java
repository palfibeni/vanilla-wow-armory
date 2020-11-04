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
public class WowheadObjectDetailsDTO {
    private String icon;
    private String name;
    private String quality;
    private String tooltip;
}
