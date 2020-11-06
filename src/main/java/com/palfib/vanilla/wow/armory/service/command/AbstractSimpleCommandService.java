package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.exception.AbstractVanillaWowArmoryException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;

public abstract class AbstractSimpleCommandService extends AbstractCommandService {

    /**
     * Generates a Command for the CommandClientBuilder
     *
     * @return jdautilities Command ready to use.
     */
    public Command generateCommand() {
        return new CommandBuilder().setName(getCommandName())
                .setBotPermissions(getPermissions())
                .setGuildOnly(isGuildOnly())
                .setAliases(getAliases())
                .setHelp(getHelp())
                .build(this::handleCommandExecution);
    }

    /**
     * Executes the Command.
     *
     * @param event input Command parameters from the user.
     */
    protected void handleCommandExecution(final CommandEvent event) {
        try {
            logUserEntrance(event);
            validateArguments(event);
            executeCommand(event);
        } catch (AbstractVanillaWowArmoryException ex) {
            event.replyWarning(ex.getMessage());
        }
    }

    protected abstract void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException;
}
