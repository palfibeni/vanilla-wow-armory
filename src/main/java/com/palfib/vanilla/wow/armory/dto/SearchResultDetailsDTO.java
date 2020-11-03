package com.palfib.vanilla.wow.armory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultDetailsDTO {
    private String icon;
    private String name;
    private String quality;
    private String tooltip;
}
