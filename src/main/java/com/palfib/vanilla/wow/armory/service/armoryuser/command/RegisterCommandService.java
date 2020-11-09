package com.palfib.vanilla.wow.armory.service.armoryuser.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.service.armoryuser.ArmoryUserService;
import com.palfib.vanilla.wow.armory.service.common.command.AbstractSimpleCommandService;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Responsible for the $register command's functionality.
 */
@Component
public class RegisterCommandService extends AbstractSimpleCommandService {

    private final ArmoryUserService userService;


    public RegisterCommandService(final ArmoryUserService userService) {
        this.userService = userService;
    }

    @Override
    protected String getCommandName() {
        return "register";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("r", "reg");
    }

    @Override
    protected String getHelp() {
        return "Register to the application";
    }

    @Override
    protected void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val author = event.getAuthor();
        val userWrapper = DiscordUserWrapper.builder()
                .discordUserId(author.getId())
                .username(author.getName())
                .discordServerId(event.getGuild().getId())
                .build();
        val discordUser = userService.saveDiscordUser(userWrapper);
        eventReply(event, String.format("User created: %s.", discordUser.getUsername()));
    }
}
