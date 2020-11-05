package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionSequenceWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.CharacterService;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class CreateCharacterCommandService extends AbstractInteractiveCommandService {

    private static final String NAME = "NAME";
    private static final String RACE = "RACE";
    private static final String CLASS = "CLASS";
    private static final String LEVEL = "LEVEL";

    private final CharacterService characterService;

    public CreateCharacterCommandService(final CharacterService characterService) {
        this.characterService = characterService;
    }

    @Override
    protected String getCommandName() {
        return "create";
    }

    @Override
    protected void validateArguments(CommandEvent event) throws VanillaWowArmoryValidationException {
        val args = event.getArgs().trim().replaceAll("\\s+", " ").split(" ");
        val argList = Arrays.asList(args);
        if (argList.size() > 4) {
            throw new VanillaWowArmoryValidationException(log, "Too many arguments");
        }
    }

    @Override
    protected void executeCommand(final CommandEvent event, final EventWaiter eventWaiter) {
        val questionSequenceWrapper = DiscordQuestionSequenceWrapper.builder()
                .event(event)
                .eventWaiter(eventWaiter)
                .questions(generateQuestionWrappers())
                .build();

        askQuestionSequence(questionSequenceWrapper, this::handleAnswers);
    }

    private Map<String, DiscordQuestionWrapper> generateQuestionWrappers() {
        val map = new HashMap<String, DiscordQuestionWrapper>();
        map.put(NAME, new DiscordQuestionWrapper("What is your character's name?", this::validateName));
        map.put(RACE, new DiscordQuestionWrapper("What is your character's race?", this::validateRace));
        map.put(CLASS, new DiscordQuestionWrapper("What is your character's class?", this::validateCharacterClass));
        map.put(LEVEL, new DiscordQuestionWrapper("What is your character's level?", this::validateLevel));
        return map;
    }

    private String validateName(final String name) {
        if (StringUtils.isEmpty(name)) {
            return "Name cannot be empty!";
        }
        if (StringUtils.containsWhitespace(name)) {
            return "Name cannot contain whitespace";
        }
        if (name.length() < 2) {
            return "Name cannot have less then 2 character.";
        }
        if (name.length() > 12) {
            return "Name cannot have more then 12 character.";
        }
        return null;
    }

    private String validateRace(final String race) {
        if (Race.parseAsEnum(race) == null) {
            return "Race cannot be empty.";
        }
        return null;
    }

    private String validateCharacterClass(final String characterClass) {
        if (CharacterClass.parseAsEnum(characterClass) == null) {
            return "CharacterClass cannot be empty.";
        }
        return null;
    }

    private String validateLevel(final String level) {
        try {
            val longValue = Long.parseLong(level);
            if (longValue < 1) {
                return "Level cannot be less then 1.";
            }
            if (longValue > 60) {
                return "Level cannot be more then 60.";
            }
        } catch (NumberFormatException ex) {
            return "Not a valid number!";
        }
        return null;
    }

    private void handleAnswers(final DiscordQuestionSequenceWrapper questionSequenceWrapper) {
        val event = questionSequenceWrapper.getEvent();
        try {
            val author = event.getAuthor();
            val characterWrapper = CharacterWrapper.builder()
                    .discordUserId(author.getId())
                    .discordUsername(author.getName())
                    .name(questionSequenceWrapper.getQuestions().get(NAME).getAnswer())
                    .race(Race.parseAsEnum(questionSequenceWrapper.getQuestions().get(RACE).getAnswer()))
                    .characterClass(CharacterClass.parseAsEnum(questionSequenceWrapper.getQuestions().get(CLASS).getAnswer()))
                    .level(Long.parseLong(questionSequenceWrapper.getQuestions().get(LEVEL).getAnswer()))
                    .build();

            characterService.save(characterWrapper);
            eventReply(event, String.format("%s has joined to the %s!", characterWrapper.getName(), characterWrapper.getRace().getFraction().getName()));
        } catch (final VanillaWowArmoryServiceException ex) {
            event.replyWarning(ex.getMessage());
        }
    }
}
