package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.dto.WowheadObjectDetailsDTO;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSearchResultDTO;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSuggestionDTO;
import com.palfib.vanilla.wow.armory.data.wrapper.WowheadSearchResultWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import reactor.util.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Responsible for reaching the public API endpoints of the wowhead.classic.com,
 * and parsing the responses.
 */
@Component
public class WowheadSearchService extends AbstractService {

    private static final String WOWHEAD_BASE_URL = "https://classic.wowhead.com";
    private static final String SEARCH_PATH = "/search/suggestions-template";
    private static final String TOOLTIP_PATH = "/tooltip/";
    private static final String ICON_BASE_URL = "https://wow.zamimg.com";
    private static final String ICON_PATH = "/images/wow/icons/medium";

    private final HttpService httpService;

    public WowheadSearchService(final HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * Searches on wowhead.classic.com, and if there is a suggestion available with given searchText, tries to fetch the
     * first object's details and returning it parsed.
     *
     * @param searchText needle for the search
     * @return search result object
     * @throws VanillaWowArmoryServiceException if there is no result in the suggestion or the details call,
     *                                          we throw an exception, with user-friendly message.
     */
    public WowheadSearchResultWrapper searchOnWowhead(final String searchText) throws VanillaWowArmoryServiceException {
        val results = startSearchOnWowhead(searchText);
        val firstResult = results.get(0);
        val objectDTO = fetchObjectDetailsFromWowhead(firstResult.getId(), firstResult.getTypeName());
        val details = parseHtmlTooltip(objectDTO);
        return WowheadSearchResultWrapper.builder()
                .wowheadSuggestionDTO(firstResult)
                .iconUrl(generateIconUrl(firstResult))
                .details(details).build();
    }

    /**
     * Looking for suggestions on wowhead.classic.com with the given searchText.
     *
     * @param searchText needle for the search
     * @return All suggestions returned from wowhead API.
     * @throws VanillaWowArmoryServiceException if there is no result in the suggestion call,
     *                                          we throw an exception, with user-friendly message.
     */
    List<WowheadSuggestionDTO> startSearchOnWowhead(final String searchText) throws VanillaWowArmoryServiceException {
        val searchResponse = httpService.get(
                WowheadSearchService.WOWHEAD_BASE_URL,
                getSuggestionPathBuilderFunction(searchText),
                WowheadSearchResultDTO.class);
        val optResults = Optional.ofNullable(searchResponse).map(WowheadSearchResultDTO::getResults);
        if (optResults.isEmpty() || optResults.get().isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("No results found for %s", searchText));
        }
        return searchResponse.getResults();
    }

    /**
     * Looking for object details on wowhead.classic.com with the given object's type, and id.
     *
     * @param objectId object's wowhead ID.
     * @param typeName object's type in string.
     * @return Object's details returned from wowhead API.
     * @throws VanillaWowArmoryServiceException if there is no result in the details call,
     *                                          we throw an exception, with user-friendly message.
     */
    WowheadObjectDetailsDTO fetchObjectDetailsFromWowhead(final Integer objectId, final String typeName) throws VanillaWowArmoryServiceException {
        val objectDTO = httpService.get(
                WowheadSearchService.WOWHEAD_BASE_URL,
                getTooltipPathBuilderFunction(typeName, objectId),
                WowheadObjectDetailsDTO.class);
        if (Objects.isNull(objectDTO)) {
            throw new VanillaWowArmoryServiceException(log, String.format("No results found for %s (%s)", objectId, typeName));
        }
        return objectDTO;
    }

    private Function<UriBuilder, URI> getSuggestionPathBuilderFunction(final String searchText) {
        return uriBuilder -> uriBuilder.path(SEARCH_PATH).queryParam("q", searchText).build();
    }

    private Function<UriBuilder, URI> getTooltipPathBuilderFunction(final String typeName, final Integer objectId) {
        return uriBuilder -> uriBuilder.path(TOOLTIP_PATH + '/' + typeName.toLowerCase().replaceAll(" ", "-") + '/' + objectId).build();
    }

    private String parseHtmlTooltip(final WowheadObjectDetailsDTO objectDTO) {
        return objectDTO.getTooltip().replaceAll("<!--[^->]+-->", " ")
                .replaceAll("<br>", "::")
                .replaceAll("</div>", "::")
                .replaceAll("<br />", "::")
                .replaceAll("<br>", "::")
                .replaceAll("<[^>]+>", " ")
                .replaceAll(" &lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .replaceAll(" ::", "::")
                .replaceAll(":: ", "::")
                .replaceAll("(::)+", "::")
                .replaceAll("::", "\n")
                .trim();
    }

    private String generateIconUrl(final WowheadSuggestionDTO firstResult) {
        final String icon = firstResult.getIcon();
        return StringUtils.isEmpty(icon) ? "" : ICON_BASE_URL + ICON_PATH + "/" + icon + ".jpg";
    }
}
