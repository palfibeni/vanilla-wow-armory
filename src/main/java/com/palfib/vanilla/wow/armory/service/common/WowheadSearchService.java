package com.palfib.vanilla.wow.armory.service.common;

import com.palfib.vanilla.wow.armory.data.enums.Raid;
import com.palfib.vanilla.wow.armory.data.enums.WowHeadResultType;
import com.palfib.vanilla.wow.armory.data.dto.WowheadObjectDetailsDTO;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSearchResultDTO;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSuggestionDTO;
import com.palfib.vanilla.wow.armory.data.wrapper.BossDetailWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.WowheadSearchResultWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Responsible for reaching the public API endpoints of the wowhead.classic.com,
 * and parsing the responses.
 */
@Component
public class WowheadSearchService extends AbstractService {

    private static final String WOWHEAD_BASE_URL = "https://classic.wowhead.com";
    private static final String SEARCH_PATH = "/search/suggestions-template";
    private static final String TOOLTIP_PATH = "/tooltip";
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
        try {
            val index = results.size() > 1 ? getResultIndex(searchText) : 0;
            val result = results.get(index);
            val resultId = result.getId();
            val typeName = result.getTypeName();
            val objectDTO = fetchObjectDetailsFromWowhead(resultId, typeName);
            val details = getDetails(objectDTO, typeName, resultId);
            return WowheadSearchResultWrapper.builder()
                    .wowheadSuggestionDTO(result)
                    .iconUrl(generateIconUrl(result).orElse(null))
                    .details(details).build();
        } catch (final NumberFormatException e) {
            return getSearchSuggestionsWrapper(results);
        }
    }

    private Integer getResultIndex(final String searchText) throws NumberFormatException {
        val searchWords = searchText.split(" ");
        return Integer.parseInt(searchWords[searchWords.length - 1]);
    }

    private WowheadSearchResultWrapper getSearchSuggestionsWrapper(final List<WowheadSuggestionDTO> suggestions) {
        String details = IntStream.range(0, suggestions.size()).mapToObj(index -> {
            val suggestion = suggestions.get(index);
            val objectLink = String.format("[%s](%s%s%s)", suggestion.getName(), WOWHEAD_BASE_URL, getObjectTypeIdAsPath(suggestion.getTypeName(), suggestion.getId()), getObjectNameAsPath(suggestion.getName()));
            return index + ". " + objectLink + " (" + suggestion.getTypeName() + ")";
        }).collect(Collectors.joining("\n"));
        return WowheadSearchResultWrapper.builder().details(details).build();
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
        return searchResponse.getResults().stream()
                .filter(suggestion -> !WowHeadResultType.ITEM_APPEARANCE_SET.equals(suggestion.getTypeName()))
                .collect(Collectors.toList());
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
    WowheadObjectDetailsDTO fetchObjectDetailsFromWowhead(final Integer objectId, final WowHeadResultType typeName) throws VanillaWowArmoryServiceException {
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

    private Function<UriBuilder, URI> getTooltipPathBuilderFunction(final WowHeadResultType typeName, final Integer objectId) {
        return uriBuilder -> uriBuilder.path(TOOLTIP_PATH + '/' + typeName.toString().toLowerCase().replaceAll(" ", "-") + '/' + objectId).build();
    }

    private String getDetails(final WowheadObjectDetailsDTO objectDTO, final WowHeadResultType typeName, final Integer id) {
        val objectLink = String.format("[%s](%s%s%s)", objectDTO.getName(), WOWHEAD_BASE_URL, getObjectTypeIdAsPath(typeName, id), getObjectNameAsPath(objectDTO.getName()));
        val details = objectDTO.getTooltip()
                .trim()
                .replaceAll("<br>|</div>|<br />", "::")
                .replaceAll("<[^>]+>|&nbsp;", " ")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("\\s+", " ")
                .replaceAll(" ::|:: ", "::")
                .replaceAll("(::)+", "::")
                .replaceAll("::", "\n")
                .trim()
                .replace(objectDTO.getName(), objectLink);
        val detailsBuilder = new StringBuilder(details);
        // For Raid type it is not reliable yet. :(
        if (details.contains("Dungeon") || details.contains("Raid")) {
            val zoneBosses = getDungeonBosses(objectDTO.getName());
            if (!zoneBosses.isEmpty()) {
                detailsBuilder.append("\n Bosses: \n");
                detailsBuilder.append(zoneBosses.stream().map(BossDetailWrapper::toString).collect(Collectors.joining("\n")));
            }
        }
        return detailsBuilder.toString();
    }

    private List<BossDetailWrapper> getDungeonBosses(final String zoneName) {
        val zoneDetails = httpService.get(
                WOWHEAD_BASE_URL,
                uriBuilder -> uriBuilder.path(getObjectNameAsPath(zoneName)).build(),
                String.class);
        val scriptPattern = Pattern.compile("<script>[^W]*(WH.Gatherer.addData\\(([^(]*)\\))+[^<]*</script><script>([^<]*)</script>");
        val scriptMatcher = scriptPattern.matcher(zoneDetails);
        if (!scriptMatcher.find()) {
            return Collections.emptyList();
        }
        if (scriptMatcher.group(2).isEmpty()) {
            return Collections.emptyList();
        }
        val bossPattern = Pattern.compile("\"(\\d+)\":\\{\"name_enus\":\"([^\"]+)\"},{0,1}");
        val bossMatcher = bossPattern.matcher(scriptMatcher.group(2).split(", ")[2]);
        val result = new ArrayList<BossDetailWrapper>();
        while (bossMatcher.find()) {
            String id = bossMatcher.group(1);
            String name = bossMatcher.group(2);
            result.add(BossDetailWrapper.builder().id(id).name(name).build());
        }
        return result;
    }

    private String getObjectTypeIdAsPath(final WowHeadResultType typeName, final Integer id) {
        if (WowHeadResultType.ZONE.equals(typeName)) {
            return "";
        }
        return "/" + typeName.getUri() + "=" + id;
    }

    private String getObjectNameAsPath(final String zoneName) {
        val raid = Raid.getByName(zoneName);
        if (Objects.nonNull(raid)) {
            return "/" + raid.getUri();
        }
        return "/" + zoneName.toLowerCase().replaceAll("'", "").replaceAll(" ", "-");
    }

    private Optional<String> generateIconUrl(final WowheadSuggestionDTO firstResult) {
        final String icon = firstResult.getIcon();
        return StringUtils.isEmpty(icon) ? Optional.empty() : Optional.of(ICON_BASE_URL + ICON_PATH + "/" + icon + ".jpg");
    }
}
