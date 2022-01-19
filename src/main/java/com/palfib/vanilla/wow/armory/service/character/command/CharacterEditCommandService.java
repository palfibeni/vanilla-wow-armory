package com.palfib.vanilla.wow.armory.service.character.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionSequenceWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.character.CharacterService;
import com.palfib.vanilla.wow.armory.service.common.command.AbstractInteractiveCommandService;
import com.palfib.vanilla.wow.armory.service.common.validator.CharacterValidatorService;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Responsible for the $character-delete command's functionality.
 */
@Component
public class CharacterEditCommandService extends AbstractInteractiveCommandService {

    private static final String CHARACTER_NAME = "CHARACTER_NAME";
    private static final String RACE = "RACE";
    private static final String CLASS = "CLASS";
    private static final String LEVEL = "LEVEL";

    private final CharacterService characterService;
    private final CharacterValidatorService characterValidatorService;

    public CharacterEditCommandService(final CharacterService characterService,
                                       final CharacterValidatorService characterValidatorService) {
        this.characterService = characterService;
        this.characterValidatorService = characterValidatorService;
    }

    @Override
    protected String getCommandName() {
        return "character-edit";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("ce", "edit");
    }

    @Override
    protected String getHelp() {
        return "Edit your character";
    }

    @Override
    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {
        val argList = parseArguments(event);
        if (argList.size() > 4) {
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
        val map = new LinkedHashMap<String, DiscordQuestionWrapper>();
        map.put(CHARACTER_NAME, new DiscordQuestionWrapper("Which character would you like to modify?", characterValidatorService::validateSimpleName));
        map.put(RACE, new DiscordQuestionWrapper("What is your character's new race?", characterValidatorService::validateRace));
        map.put(CLASS, new DiscordQuestionWrapper("What is your character's new class?", characterValidatorService::validateCharacterClass));
        map.put(LEVEL, new DiscordQuestionWrapper("What is your character's new level?", characterValidatorService::validateLevel));
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
            val characterWrapper = CharacterWrapper.CharacterWrapperBuilder()
                    .discordUserId(author.getId())
                    .discordUsername(author.getName())
                    .characterName(questionSequenceWrapper.getQuestions().get(CHARACTER_NAME).getAnswer())
                    .race(Race.parseAsEnum(questionSequenceWrapper.getQuestions().get(RACE).getAnswer()))
                    .characterClass(CharacterClass.parseAsEnum(questionSequenceWrapper.getQuestions().get(CLASS).getAnswer()))
                    .level(Long.parseLong(questionSequenceWrapper.getQuestions().get(LEVEL).getAnswer()))
                    .build();

            characterService.update(characterWrapper);
            eventReply(event, String.format("%s is updated!", characterWrapper.getCharacterName()));
        } catch (final VanillaWowArmoryServiceException ex) {
            event.replyWarning(ex.getMessage());
        }
    }
}
