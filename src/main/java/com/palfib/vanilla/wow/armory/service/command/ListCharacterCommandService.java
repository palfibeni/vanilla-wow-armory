package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.data.entity.Character;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.service.CharacterService;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for the $character-list command's functionality.
 */
@Component
public class ListCharacterCommandService extends AbstractSimpleCommandService {

    private final DiscordUserService discordUserService;
    private final CharacterService characterService;

    public ListCharacterCommandService(final DiscordUserService discordUserService,
                                       final CharacterService characterService) {
        this.discordUserService = discordUserService;
        this.characterService = characterService;
    }

    @Override
    protected String getCommandName() {
        return "character-list";
    }


    @Override
    protected List<String> getAliases() {
        return List.of("cl", "list");
    }

    @Override
    protected String getHelp() {
        return "List self, or mentioned user's characters";
    }

    @Override
    protected void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val armoryUser = discordUserService.getMentionedOrCurrentUser(event);
        val characters = characterService.listByUser(armoryUser);
        val response = new EmbedBuilder()
                .setTitle(String.format("%s has %d characters:", armoryUser.getUsername(), characters.size()))
                .setDescription(characters.stream().map(Character::toString).collect(Collectors.joining("\n")))
                .build();
        eventReply(event, response);
    }
}
