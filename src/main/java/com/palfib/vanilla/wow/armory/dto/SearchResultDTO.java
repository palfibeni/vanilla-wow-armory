package com.palfib.vanilla.wow.armory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultDTO {
    private Integer type;
    private Integer id;
    private String name;
    private String typeName;
    private String icon;
    private String quality;
}
