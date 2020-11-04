package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.service.DiscordUserService;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Responsible for the $register command's functionality.
 */
@Component
public class RegisterCommandService extends AbstractCommandService {

    private final DiscordUserService discordUserService;


    public RegisterCommandService(final DiscordUserService discordUserService) {
        this.discordUserService = discordUserService;
    }

    @Override
    protected String getCommandName() {
        return "register";
    }

    @Override
    protected List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    protected String generateEntryLog(final CommandEvent event) {
        return String.format("New registration initiated from: %s", event.getAuthor().getName());
    }

    @Override
    protected void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val author = event.getAuthor();
        val userWrapper = DiscordUserWrapper.builder()
                .id(Long.parseLong(author.getId()))
                .username(author.getName())
                .serverId(Long.parseLong(event.getGuild().getId()))
                .build();
        val discordUser = discordUserService.save(userWrapper);
        val response = new EmbedBuilder()
                .setTitle(String.format("User created: %s.", discordUser.getUsername()))
                .build();
        eventReply(event, response);
    }
}
