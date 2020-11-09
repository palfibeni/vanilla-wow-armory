package com.palfib.vanilla.wow.armory.service;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.service.command.*;
import lombok.val;
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
public class DiscordBotService extends AbstractService {

    private final Environment environment;
    private final SearchCommandService searchCommandService;
    private final RegisterCommandService registerCommandService;
    private final CreateCharacterCommandService createCharacterCommandService;
    private final ListCharacterCommandService listCharacterCommandService;
    private final CharacterTalentCommandService characterTalentCommandService;

    public DiscordBotService(final Environment environment,
                             final SearchCommandService searchCommandService,
                             final RegisterCommandService registerCommandService,
                             final CreateCharacterCommandService createCharacterCommandService,
                             final ListCharacterCommandService listCharacterCommandService,
                             final CharacterTalentCommandService characterTalentCommandService) {
        this.environment = environment;
        this.searchCommandService = searchCommandService;
        this.registerCommandService = registerCommandService;
        this.createCharacterCommandService = createCharacterCommandService;
        this.listCharacterCommandService = listCharacterCommandService;
        this.characterTalentCommandService = characterTalentCommandService;
    }

    /**
     * When the Application is ready, Spring Application Context is loaded, start initializing the Discord Bot.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeBot() {
        log.info("Initialize Vanilla WoW Armory Discord Bot");
        try {
            val api = JDABuilder
                    .createDefault(environment.getProperty("discordbot.token"))
                    .build();

            val eventWaiter = new EventWaiter();
            val commandClient = createCommandClient(eventWaiter);
            api.addEventListener(eventWaiter, commandClient);
            api.awaitReady();
        } catch (InterruptedException | LoginException e) {
            log.error("Error during Vanilla WoW Armory Discord Bot Initialization");
            e.printStackTrace();
        }
    }

    /**
     * Create a CommandClient which listens to multiple Discord commands.
     *
     * @return newly created CommandClient
     */
    private CommandClient createCommandClient(final EventWaiter eventWaiter) {
        val ccb = new CommandClientBuilder()
                .setPrefix(environment.getProperty("discordbot.prefix", "$"))
                .setStatus(OnlineStatus.ONLINE)
                .setOwnerId(environment.getProperty("discordbot.ownerId"))
                .setActivity(Activity.listening(environment.getProperty("discordbot.activityMessage", "$help")))
                .addCommands(generateCommands(eventWaiter));
        return ccb.build();
    }

    private Command[] generateCommands(final EventWaiter eventWaiter) {
        return new Command[]{
                searchCommandService.generateCommand(),
                registerCommandService.generateCommand(),
                createCharacterCommandService.generateCommand(eventWaiter),
                listCharacterCommandService.generateCommand(),
                characterTalentCommandService.generateCommand(eventWaiter),
        };
    }
}
