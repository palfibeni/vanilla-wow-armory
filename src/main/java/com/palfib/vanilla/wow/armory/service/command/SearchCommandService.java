package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.data.wrapper.WowheadSearchResult;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.WowheadSearchService;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.util.StringUtils;

import java.util.List;

/**
 * Responsible for the !search command's functionality.
 */
@Component
public class SearchCommandService extends AbstractCommandService {

    private static final Logger log = LoggerFactory.getLogger(SearchCommandService.class);

    private final WowheadSearchService wowheadSearchService;

    public SearchCommandService(final WowheadSearchService wowheadSearchService) {
        this.wowheadSearchService = wowheadSearchService;
    }

    @Override
    protected String getCommandName() {
        return "search";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("saerch", "saehrc", "saerhc", "searhc", "seahrc");
    }

    @Override
    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {
        if (StringUtils.isEmpty(event.getArgs())) {
            throw new VanillaWowArmoryValidationException(log, String.format("%s didn't give me any searchText!", event.getAuthor().getName()));
        }
    }

    @Override
    protected void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val arguments = event.getArgs();
        val authorName = event.getAuthor().getName();
        log.info(String.format("New search initiated from: %s, with: %s", authorName, arguments));
        val searchResult = wowheadSearchService.searchOnWowhead(arguments);
        val response = new EmbedBuilder()
                .setTitle(String.format("%s searched for: %s (%s)", authorName, searchResult.getName(), searchResult.getTypeName()))
                .setDescription(searchResult.getDetails())
                .setThumbnail(searchResult.getIconUrl())
                .build();
        event.reply(response);
    }
}
