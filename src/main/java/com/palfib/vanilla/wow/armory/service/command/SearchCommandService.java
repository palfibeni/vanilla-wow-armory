package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.WowheadSearchService;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;
import reactor.util.StringUtils;

import java.util.List;

/**
 * Responsible for the $search command's functionality.
 */
@Component
public class SearchCommandService extends AbstractCommandService {

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
    protected String generateEntryLog(final CommandEvent event) {
        return String.format("New search initiated from: %s, with: %s", event.getAuthor().getName(), event.getArgs());
    }

    @Override
    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {
        if (StringUtils.isEmpty(event.getArgs())) {
            throw new VanillaWowArmoryValidationException(log, String.format("%s didn't give me any searchText!", event.getAuthor().getName()));
        }
    }

    @Override
    protected void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val searchResult = wowheadSearchService.searchOnWowhead(event.getArgs());
        val response = new EmbedBuilder()
                .setTitle(String.format("%s searched for: %s (%s)", event.getAuthor().getName(), searchResult.getName(), searchResult.getTypeName()))
                .setDescription(searchResult.getDetails())
                .setThumbnail(searchResult.getIconUrl())
                .build();
        eventReply(event, response);
    }
}
