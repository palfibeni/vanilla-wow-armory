package com.palfib.vanilla.wow.armory.service.common.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.service.common.AbstractService;
import com.palfib.vanilla.wow.armory.service.armoryuser.ArmoryUserService;
import lombok.val;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class DiscordUserService extends AbstractService {

    private final ArmoryUserService armoryUserService;

    protected DiscordUserService(final ArmoryUserService armoryUserService) {
        this.armoryUserService = armoryUserService;
    }

    /**
     * Finds ArmoryUser by CommandEvent.author
     *
     * @param event Discord Message event
     * @return ArmoryUser from database
     * @throws VanillaWowArmoryServiceException if user is not registered.
     */
    public ArmoryUser getCurrentUser(final CommandEvent event) throws VanillaWowArmoryServiceException {
        return getArmoryUserByDiscordUser(event.getAuthor());
    }

    /**
     * Finds ArmoryUser by CommandEvent.mentionedUsers
     *
     * @param event Discord Message event
     * @return ArmoryUser from database
     * @throws VanillaWowArmoryServiceException if user is not registered, or there is no mention in the message.
     */
    public ArmoryUser getMentionedUser(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val optionalUserMention = event.getMessage().getMentionedUsers().stream().findFirst();
        if (optionalUserMention.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, "You need to mention an other user to use this command");
        }
        return getArmoryUserByDiscordUser(optionalUserMention.get());
    }

    /**
     * Finds ArmoryUser by CommandEvent.mentionedUsers or CommandEvent.author
     *
     * @param event Discord Message event
     * @return ArmoryUser from database
     * @throws VanillaWowArmoryServiceException if both search results in no registered user.
     */
    public ArmoryUser getMentionedOrCurrentUser(final CommandEvent event) throws VanillaWowArmoryServiceException {
        val discordUser = event.getMessage().getMentionedUsers().stream()
                .findFirst()
                .orElse(event.getAuthor());
        return getArmoryUserByDiscordUser(discordUser);
    }

    /**
     * Finds ArmoryUser by Discord User
     *
     * @param discordUser Discord User
     * @return ArmoryUser from database
     * @throws VanillaWowArmoryServiceException if the user is not registered yet.
     */
    private ArmoryUser getArmoryUserByDiscordUser(final User discordUser) throws VanillaWowArmoryServiceException {
        val optArmoryUser = armoryUserService.findByDiscordUserId(discordUser.getId());
        if (optArmoryUser.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s user has not registered yet.", discordUser.getName()));
        }
        return optArmoryUser.get();
    }
}
