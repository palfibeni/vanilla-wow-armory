package com.palfib.vanilla.wow.armory.service;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.service.command.SearchCommandService;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;

/**
 * Initializes the Discord bot.
 */
@Component
public class DiscordBotService {

    private final Environment environment;
    private final SearchCommandService searchCommandService;

    public DiscordBotService(final Environment environment, final SearchCommandService searchCommandService) {
        this.environment = environment;
        this.searchCommandService = searchCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeBot() {
        try {
            val api = JDABuilder
                    .createDefault(environment.getProperty("discordbot.token"))
                    .build();

            val eventWaiter = new EventWaiter();
            val commandClient = createCommandClient();
            api.addEventListener(eventWaiter, commandClient);
            api.awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
    }

    private CommandClient createCommandClient() {
        val ccb = new CommandClientBuilder()
                .setPrefix(environment.getProperty("discordbot.prefix", "$"))
                .setStatus(OnlineStatus.ONLINE)
                .setOwnerId(environment.getProperty("discordbot.ownerId"))
                .setActivity(Activity.listening(environment.getProperty("discordbot.activityMessage", "for Warchief")))
                .addCommands(generateCommands());
        return ccb.build();
    }

    private Command[] generateCommands() {
        return new Command[]{
                searchCommandService.generateCommand(),
        };
    }
}
