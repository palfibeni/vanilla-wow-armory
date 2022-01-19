package com.palfib.vanilla.wow.armory.service.character.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterTalentWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionSequenceWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryValidationException;
import com.palfib.vanilla.wow.armory.service.character.CharacterService;
import com.palfib.vanilla.wow.armory.service.character.CharacterTalentService;
import com.palfib.vanilla.wow.armory.service.common.command.AbstractInteractiveCommandService;
import com.palfib.vanilla.wow.armory.service.common.command.DiscordUserService;
import com.palfib.vanilla.wow.armory.service.common.validator.CharacterValidatorService;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Responsible for the $character-talent command's functionality.
 */
@Component
public class CharacterTalentCommandService extends AbstractInteractiveCommandService {

    private static final String CHARACTER_NAME = "CHARACTER_NAME";
    private static final String NAME = "NAME";
    private static final String TALENT = "TALENT";

    private static final String CLASSIC_WOWHEAD_TALENT_CALC = "https://classic.wowhead.com/talent-calc";
    private static final String TALENT_CLACULATOR_REGEX = "^" + CLASSIC_WOWHEAD_TALENT_CALC + "/\\w+/[0-9]*(-[0-9]*){0,2}$";

    private final DiscordUserService discordUserService;
    private final CharacterService characterService;
    private final CharacterTalentService characterTalentService;
    private final CharacterValidatorService characterValidatorService;

    public CharacterTalentCommandService(final DiscordUserService discordUserService,
                                         final CharacterService characterService,
                                         final CharacterTalentService characterTalentService,
                                         final CharacterValidatorService characterValidatorService) {
        this.discordUserService = discordUserService;
        this.characterService = characterService;
        this.characterTalentService = characterTalentService;
        this.characterValidatorService = characterValidatorService;
    }

    @Override
    protected String getCommandName() {
        return "character-talent";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("ct", "talent");
    }

    @Override
    protected String getHelp() {
        return "Set talent to your character by adding a link, generated with https://classic.wowhead.com/talent-calc";
    }

    @Override
    protected void validateArguments(final CommandEvent event) throws VanillaWowArmoryValidationException {
        val argList = parseArguments(event);
        if (argList.size() > 3) {
            throw new VanillaWowArmoryValidationException(log, "Too many arguments");
        }
    }

    @Override
    protected void executeCommand(final CommandEvent event, final EventWaiter eventWaiter) throws VanillaWowArmoryServiceException {
        val currentUser = discordUserService.getCurrentUser(event);
        val arguments = parseArguments(event);
        val characterName = arguments.remove(0);
        val optCharacter = characterService.findByUserAndName(currentUser, characterName);
        if (optCharacter.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s user has no character named %s.", currentUser.getUsername(), characterName));
        }
        val questionSequenceWrapper = DiscordQuestionSequenceWrapper.builder()
                .event(event)
                .eventWaiter(eventWaiter)
                .questions(generateQuestionWrappers(characterName, arguments))
                .build();

        askQuestionSequence(questionSequenceWrapper, this::handleAnswers);
    }

    private Map<String, DiscordQuestionWrapper> generateQuestionWrappers(final String characterName, final List<String> argList) {
        val map = new LinkedHashMap<String, DiscordQuestionWrapper>();
        map.put(CHARACTER_NAME, new DiscordQuestionWrapper("What is your character's name?", characterValidatorService::validateSimpleName));
        map.get(CHARACTER_NAME).setAnswer(characterName);
        map.put(TALENT, new DiscordQuestionWrapper("What is your talent's url?", this::validateTalent));
        map.put(NAME, new DiscordQuestionWrapper("What is your spec's name?", characterValidatorService::validateSimpleName));
        argList.forEach(argument -> map.values().stream()
                .filter(question -> question.isFreeToAsk() && StringUtils.isEmpty(question.getValidator().apply(argument)))
                .findFirst()
                .ifPresent(question -> question.setAnswer(argument))
        );
        return map;
    }


    private String validateTalent(final String talent) {
        if (!Pattern.matches(TALENT_CLACULATOR_REGEX, talent)) {
            return String.format("Talent has to be generated on %s", CLASSIC_WOWHEAD_TALENT_CALC);
        }
        return null;
    }

    private void handleAnswers(final DiscordQuestionSequenceWrapper questionSequenceWrapper) {
        val event = questionSequenceWrapper.getEvent();
        try {
            val author = event.getAuthor();
            val characterTalentWrapper = CharacterTalentWrapper.CharacterTalentWrapperBuilder()
                    .discordUserId(author.getId())
                    .discordUsername(author.getName())
                    .characterName(questionSequenceWrapper.getQuestions().get(CHARACTER_NAME).getAnswer())
                    .name(StringUtils.capitalize(questionSequenceWrapper.getQuestions().get(NAME).getAnswer()))
                    .talent(questionSequenceWrapper.getQuestions().get(TALENT).getAnswer())
                    .build();

            characterTalentService.save(characterTalentWrapper);
            eventReply(event, String.format("%s is on %s talent!", characterTalentWrapper.getCharacterName(), characterTalentWrapper.getName()));
        } catch (final VanillaWowArmoryServiceException ex) {
            event.replyWarning(ex.getMessage());
        }
    }
}
