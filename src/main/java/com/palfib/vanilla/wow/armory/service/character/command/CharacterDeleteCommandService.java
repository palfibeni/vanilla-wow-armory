package com.palfib.vanilla.wow.armory.service.character.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterNameWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionSequenceWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.character.CharacterService;
import com.palfib.vanilla.wow.armory.service.common.command.AbstractInteractiveCommandService;
import com.palfib.vanilla.wow.armory.service.common.command.DiscordUserService;
import com.palfib.vanilla.wow.armory.service.common.validator.CharacterValidatorService;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Responsible for the $character-delete command's functionality.
 */
@Component
public class CharacterDeleteCommandService extends AbstractInteractiveCommandService {

    private static final String CHARACTER_NAME = "CHARACTER_NAME";

    private final DiscordUserService discordUserService;
    private final CharacterService characterService;
    private final CharacterValidatorService characterValidatorService;

    public CharacterDeleteCommandService(final DiscordUserService discordUserService,
                                         final CharacterService characterService,
                                         final CharacterValidatorService characterValidatorService) {
        this.discordUserService = discordUserService;
        this.characterService = characterService;
        this.characterValidatorService = characterValidatorService;
    }

    @Override
    protected String getCommandName() {
        return "character-delete";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("cd", "delete");
    }

    @Override
    protected String getHelp() {
        return "Delete your desired character";
    }

    @Override
    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {
        val argList = parseArguments(event);
        if (argList.size() > 1) {
            throw new VanillaWowArmoryValidationException(log, "Too many arguments");
        }
    }

    @Override
    protected void executeCommand(final CommandEvent event, final EventWaiter eventWaiter) {
        val arguments = parseArguments(event);
        val questionSequenceWrapper = DiscordQuestionSequenceWrapper.builder()
                .event(event)
                .eventWaiter(eventWaiter)
                .questions(generateQuestionWrappers(arguments))
                .build();

        askQuestionSequence(questionSequenceWrapper, this::handleAnswers);
    }

    private Map<String, DiscordQuestionWrapper> generateQuestionWrappers(final List<String> argList) {
        val map = new HashMap<String, DiscordQuestionWrapper>();
        map.put(CHARACTER_NAME, new DiscordQuestionWrapper("What is your character's name?", characterValidatorService::validateSimpleName));
        argList.forEach(argument -> map.values().stream()
                .filter(question -> question.isFreeToAsk() && StringUtils.isEmpty(question.getValidator().apply(argument)))
                .findFirst()
                .ifPresent(question -> question.setAnswer(argument))
        );
        return map;
    }

    private void handleAnswers(final DiscordQuestionSequenceWrapper questionSequenceWrapper) {
        val event = questionSequenceWrapper.getEvent();
        try {
            val author = event.getAuthor();
            val characterNameWrapper = CharacterNameWrapper.builder()
                    .discordUserId(author.getId())
                    .discordUsername(author.getName())
                    .characterName(questionSequenceWrapper.getQuestions().get(CHARACTER_NAME).getAnswer())
                    .build();

            characterService.delete(characterNameWrapper);
            eventReply(event, String.format("%s is deleted!", characterNameWrapper.getCharacterName()));
        } catch (final VanillaWowArmoryServiceException ex) {
            event.replyWarning(ex.getMessage());
        }
    }
}
