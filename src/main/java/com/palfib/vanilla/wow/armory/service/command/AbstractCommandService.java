package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.AbstractService;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommandService extends AbstractService {

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

    protected abstract String getHelp();

    protected void logUserEntrance(final CommandEvent event) {
        log.info(generateEntryLog(event));
    }

    protected String generateEntryLog(final CommandEvent event) {
        return String.format("New %s command from %s.", getCommandName(), event.getAuthor().getName());
    }

    protected List<String> parseArguments(final CommandEvent event) {
        val args = event.getArgs().trim().replaceAll("\\s+", " ").split(" ");
        return new ArrayList<>(Arrays.asList(args));
    }

    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {

    }

    protected void eventReply(final CommandEvent event, final String message) {
        log.info(message);
        event.reply(message);
    }

    protected void eventReply(final CommandEvent event, final MessageEmbed message) {
        log.info(message.getTitle());
        event.reply(message);
    }

    protected String validateSimpleName(final String name) {
        if (StringUtils.isEmpty(name)) {
            return "Name cannot be empty!";
        }
        if (StringUtils.containsWhitespace(name)) {
            return "Name cannot contain whitespace";
        }
        if (name.length() < 2) {
            return "Name cannot have less then 2 character.";
        }
        if (name.length() > 15) {
            return "Name cannot have more then 15 character.";
        }
        return null;
    }
}
