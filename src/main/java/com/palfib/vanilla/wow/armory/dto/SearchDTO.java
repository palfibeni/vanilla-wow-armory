package com.palfib.vanilla.wow.armory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchDTO {
    private String search;
    private List<SearchResultDTO> results;
}
