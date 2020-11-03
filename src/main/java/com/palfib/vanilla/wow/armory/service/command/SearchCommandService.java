package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.dto.SearchResultDetailsDTO;
import com.palfib.vanilla.wow.armory.dto.SearchDTO;
import com.palfib.vanilla.wow.armory.dto.SearchResultDTO;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Component
public class SearchCommandService {

    private static final Logger log = LoggerFactory.getLogger(SearchCommandService.class);

    private static final String COMMAND_NAME = "search";
    private static final String[] ALIASES = new String[]{"saerch", "saehrc", "saerhc", "searhc", "seahrc"};

    private static final String BASE_URL = "https://classic.wowhead.com";
    private static final String SEARCH_PATH = "/search/suggestions-template";
    private static final String TOOLTIP_PATH = "/tooltip/";

    public Command getWowHeadCommand() {
        return new CommandBuilder().setName(COMMAND_NAME)
                .setBotPermissions(Permission.MESSAGE_EMBED_LINKS)
                .setGuildOnly(false)
                .setAliases(ALIASES)
                .build(this::executeCommand);
    }

    private void executeCommand(final CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyWarning("You didn't give me any searchText!");
            return;
        }

        final WebClient client = WebClient.builder().baseUrl(BASE_URL).build();

        final SearchDTO searchResponse = client.get()
                .uri(getSuggestionPathBuilderFunction(event))
                .retrieve()
                .bodyToMono(SearchDTO.class)
                .block();
        final Optional<List<SearchResultDTO>> optResults = Optional.ofNullable(searchResponse).map(SearchDTO::getResults);
        if (optResults.isEmpty() || optResults.get().isEmpty()) {
            event.replyWarning(String.format("No results found for %s", event.getArgs()));
            return;
        }

        final List<SearchResultDTO> results = searchResponse.getResults();
        final SearchResultDTO firstResult = results.get(0);

        final SearchResultDetailsDTO objectDTO = client.get()
                .uri(getTooltipPathBuilderFunction(firstResult))
                .retrieve()
                .bodyToMono(SearchResultDetailsDTO.class)
                .block();
        if (Objects.isNull(objectDTO)) {
            log.error(String.format("No results found for %s", event.getArgs()));
            event.replyWarning(String.format("No results found for %s", event.getArgs()));
            return;
        }

        final String searchResult = parseHtmlTooltip(objectDTO);
        event.reply(String.format("You searched for: %s (%s)\n %s",
                firstResult.getName(),
                firstResult.getTypeName(),
                searchResult));
    }

    private Function<UriBuilder, URI> getSuggestionPathBuilderFunction(final CommandEvent event) {
        return uriBuilder -> uriBuilder.path(SEARCH_PATH).queryParam("q", event.getArgs()).build();
    }

    private Function<UriBuilder, URI> getTooltipPathBuilderFunction(final SearchResultDTO firstResult) {
        return uriBuilder -> uriBuilder.path(TOOLTIP_PATH + '/' + firstResult.getTypeName().toLowerCase().replaceAll(" ", "-") + '/' + firstResult.getId()).build();
    }

    private String parseHtmlTooltip(final SearchResultDetailsDTO objectDTO) {
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
                .replaceAll("::", "\n");
    }
}
