package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.exception.AbstractVanillaWowArmoryException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public abstract class AbstractCommandService {

    protected final Logger log = LoggerFactory.getLogger(getClass());

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
                .build(this::handleCommandExecution);
    }

    protected abstract String getCommandName();

    protected Permission[] getPermissions() {
        return new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    protected boolean isGuildOnly() {
        return false;
    }

    protected List<String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * Executes the Command.
     *
     * @param event input Command parameters from the user.
     */
    private void handleCommandExecution(final CommandEvent event) {
        try {
            logUserEntrance(event);
            validateArguments(event);
            executeCommand(event);
        } catch (AbstractVanillaWowArmoryException ex) {
            event.replyWarning(ex.getMessage());
        }
    }

    private void logUserEntrance(final CommandEvent event) {
        log.info(generateEntryLog(event));
    }

    protected String generateEntryLog(final CommandEvent event) {
        return String.format("New %s command from %s.", getCommandName(), event.getAuthor().getName());
    }

    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {

    }

    protected abstract void executeCommand(final CommandEvent event) throws VanillaWowArmoryServiceException;

    protected void eventReply(final CommandEvent event, final MessageEmbed message) {
        log.info(message.getTitle());
        event.reply(message);
    }
}
