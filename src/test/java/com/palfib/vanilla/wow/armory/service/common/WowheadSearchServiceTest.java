package com.palfib.vanilla.wow.armory.service.common;

import com.palfib.vanilla.wow.armory.data.dto.WowheadObjectDetailsDTO;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSearchResultDTO;
import com.palfib.vanilla.wow.armory.data.dto.WowheadSuggestionDTO;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.service.common.HttpService;
import com.palfib.vanilla.wow.armory.service.common.WowheadSearchService;
import lombok.val;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WowheadSearchServiceTest {

    private static final String SEARCH_TEXT = "bonereaver";
    private static final String OBJECT_NAME = "Bonereaver's Edge";
    private static final Integer OBJECT_ID = 123;
    private static final String TYPE_NAME = "Item";
    private static final String TOOLTIP_FIRST_LINE = "Detailed tooltip to an awesome sword";
    private static final String TOOLTIP_SECOND_LINE = "an other line of description";
    private static final String TOOLTIP_HTML = "<div>" + TOOLTIP_FIRST_LINE + "</div>" + "<br>" + "<div>" + TOOLTIP_SECOND_LINE + "</div>";

    private static final List<WowheadSuggestionDTO> SUGGESTIONS = Lists.list(WowheadSuggestionDTO.builder().name(OBJECT_NAME).id(OBJECT_ID).typeName(TYPE_NAME).build());

    @Mock
    private HttpService httpService;

    @InjectMocks
    private WowheadSearchService wowheadSearchService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        wowheadSearchService = new WowheadSearchService(httpService);
    }

    @Test
    public void testSearchOnWowhead() throws VanillaWowArmoryServiceException {
        val mockSearchResult = WowheadSearchResultDTO.builder().search(SEARCH_TEXT).results(SUGGESTIONS).build();
        when(httpService.get(anyString(), any(), eq(WowheadSearchResultDTO.class))).thenReturn(mockSearchResult);

        val mockObjectDetails = WowheadObjectDetailsDTO.builder()
                .tooltip(TOOLTIP_HTML)
                .build();
        when(httpService.get(anyString(), any(), eq(WowheadObjectDetailsDTO.class))).thenReturn(mockObjectDetails);

        val result = wowheadSearchService.searchOnWowhead(SEARCH_TEXT);

        verify(httpService, times(1)).get(anyString(), any(), eq(WowheadSearchResultDTO.class));
        verify(httpService, times(1)).get(anyString(), any(), eq(WowheadObjectDetailsDTO.class));
        assertEquals(result.getDetails(), TOOLTIP_FIRST_LINE + "\n" + TOOLTIP_SECOND_LINE);
    }

    @Test
    public void testStartSearchOnWowheadWithResult() throws VanillaWowArmoryServiceException {
        val mockSearchResult = WowheadSearchResultDTO.builder().search(SEARCH_TEXT).results(SUGGESTIONS).build();
        when(httpService.get(anyString(), any(), eq(WowheadSearchResultDTO.class))).thenReturn(mockSearchResult);

        val result = wowheadSearchService.startSearchOnWowhead(SEARCH_TEXT);

        verify(httpService, times(1)).get(anyString(), any(), eq(WowheadSearchResultDTO.class));
        assertEquals(result, SUGGESTIONS);
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testStartSearchOnWowheadWithoutResult() throws VanillaWowArmoryServiceException {
        val mockSearchResult = WowheadSearchResultDTO.builder().search(SEARCH_TEXT).results(Lists.emptyList()).build();
        when(httpService.get(anyString(), any(), eq(WowheadSearchResultDTO.class))).thenReturn(mockSearchResult);

        wowheadSearchService.startSearchOnWowhead(SEARCH_TEXT);

        verify(httpService, times(1)).get(anyString(), any(), eq(WowheadSearchResultDTO.class));
    }

    @Test
    public void fetchObjectDetailsFromWowheadWithResult() throws VanillaWowArmoryServiceException {
        val mockObjectDetails = WowheadObjectDetailsDTO.builder()
                .tooltip(TOOLTIP_HTML)
                .build();
        when(httpService.get(anyString(), any(), eq(WowheadObjectDetailsDTO.class))).thenReturn(mockObjectDetails);

        val result = wowheadSearchService.fetchObjectDetailsFromWowhead(OBJECT_ID, TYPE_NAME);

        verify(httpService, times(1)).get(anyString(), any(), eq(WowheadObjectDetailsDTO.class));
        assertEquals(result, mockObjectDetails);
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void fetchObjectDetailsFromWowheadWithoutResult() throws VanillaWowArmoryServiceException {
        when(httpService.get(anyString(), any(), eq(WowheadObjectDetailsDTO.class))).thenReturn(null);

        wowheadSearchService.fetchObjectDetailsFromWowhead(OBJECT_ID, TYPE_NAME);

        verify(httpService, times(1)).get(anyString(), any(), eq(WowheadObjectDetailsDTO.class));
    }

}