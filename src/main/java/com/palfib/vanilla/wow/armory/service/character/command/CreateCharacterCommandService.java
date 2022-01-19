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
 * Responsible for the $character-create command's functionality.
 */
@Component
public class CreateCharacterCommandService extends AbstractInteractiveCommandService {

    private static final String NAME = "NAME";
    private static final String RACE = "RACE";
    private static final String CLASS = "CLASS";
    private static final String LEVEL = "LEVEL";

    private final CharacterService characterService;
    private final CharacterValidatorService characterValidatorService;

    public CreateCharacterCommandService(final CharacterService characterService,
                                         final CharacterValidatorService characterValidatorService) {
        this.characterService = characterService;
        this.characterValidatorService = characterValidatorService;
    }

    @Override
    protected String getCommandName() {
        return "character-create";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("cc", "create");
    }

    @Override
    protected String getHelp() {
        return "Create character, this will initiate a questions, which the user have 15 second to answer each";
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
        val questionSequenceWrapper = DiscordQuestionSequenceWrapper.builder()
                .event(event)
                .eventWaiter(eventWaiter)
                .questions(generateQuestionWrappers(parseArguments(event)))
                .build();

        askQuestionSequence(questionSequenceWrapper, this::handleAnswers);
    }

    private Map<String, DiscordQuestionWrapper> generateQuestionWrappers(final List<String> argList) {
        val map = new LinkedHashMap<String, DiscordQuestionWrapper>();
        map.put(NAME, new DiscordQuestionWrapper("What is your character's name?", characterValidatorService::validateSimpleName));
        map.put(RACE, new DiscordQuestionWrapper("What is your character's race?", characterValidatorService::validateRace));
        map.put(CLASS, new DiscordQuestionWrapper("What is your character's class?", characterValidatorService::validateCharacterClass));
        map.put(LEVEL, new DiscordQuestionWrapper("What is your character's level?", characterValidatorService::validateLevel));
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
                    .characterName(StringUtils.capitalize(questionSequenceWrapper.getQuestions().get(NAME).getAnswer()))
                    .race(Race.parseAsEnum(questionSequenceWrapper.getQuestions().get(RACE).getAnswer()))
                    .characterClass(CharacterClass.parseAsEnum(questionSequenceWrapper.getQuestions().get(CLASS).getAnswer()))
                    .level(Long.parseLong(questionSequenceWrapper.getQuestions().get(LEVEL).getAnswer()))
                    .build();

            characterService.save(characterWrapper);
            eventReply(event, String.format("%s has joined to the %s!", characterWrapper.getCharacterName(), characterWrapper.getRace().getFraction().getName()));
        } catch (final VanillaWowArmoryServiceException ex) {
            event.replyWarning(ex.getMessage());
        }
    }
}
