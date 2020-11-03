package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.exception.AbstractVanillaWowArmoryException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.List;

public abstract class AbstractCommandService {

    public Command generateCommand() {
        return new CommandBuilder().setName(getCommandName())
                .setBotPermissions(getPermissions())
                .setGuildOnly(isGuildOnly())
                .setAliases(getAliases())
                .build(this::handleCommandExecution);
    }

    private void handleCommandExecution(final CommandEvent event) {
        try {
            validateArguments(event);
            executeCommand(event);
        } catch (AbstractVanillaWowArmoryException ex) {
            event.replyWarning(ex.getMessage());
        }
    }

    protected boolean isGuildOnly() {
        return false;
    }

    protected abstract String getCommandName();

    protected Permission[] getPermissions() {
        return new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    ;

    protected List<String> getAliases() {
        return Collections.emptyList();
    }

    ;

    protected abstract void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException;

    protected abstract void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException;
}
