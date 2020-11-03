package com.palfib.vanilla.wow.armory.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WowheadObjectDetailsDTO {
    private String icon;
    private String name;
    private String quality;
    private String tooltip;
}
