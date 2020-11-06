package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.data.entity.Character;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.service.ArmoryUserService;
import com.palfib.vanilla.wow.armory.service.CharacterService;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListCharacterCommandService extends AbstractSimpleCommandService {

    private final ArmoryUserService armoryUserService;
    private final CharacterService characterService;

    public ListCharacterCommandService(final ArmoryUserService armoryUserService,
                                       final CharacterService characterService) {
        this.armoryUserService = armoryUserService;
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
    protected void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val discordUser = event.getMessage().getMentionedUsers().stream()
                .findFirst()
                .orElse(event.getAuthor());
        val optArmoryUser = armoryUserService.findByDiscordUserId(discordUser.getId());
        if (optArmoryUser.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s user has not registered yet.", discordUser.getName()));
        }
        val armoryUser = optArmoryUser.get();
        val characters = characterService.listByUser(armoryUser);
        val response = new EmbedBuilder()
                .setTitle(String.format("%s has %d characters:", armoryUser.getUsername(), characters.size()))
                .setDescription(characters.stream().map(Character::toString).collect(Collectors.joining("\n")))
                .build();
        eventReply(event, response);
    }
}
