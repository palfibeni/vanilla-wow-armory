package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.data.entity.Character;
import com.palfib.vanilla.wow.armory.data.entity.CharacterTalent;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterTalentWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.CharacterRepository;
import com.palfib.vanilla.wow.armory.repository.CharacterTalentRepository;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Responsible for creating and managing Character entities.
 */
@Component
public class CharacterTalentService extends AbstractService {

    private final ArmoryUserService userService;
    private final CharacterService characterService;
    private final CharacterTalentRepository characterTalentRepository;

    public CharacterTalentService(final ArmoryUserService userService, final CharacterService characterService, final CharacterTalentRepository characterTalentRepository) {
        this.characterService = characterService;
        this.userService = userService;
        this.characterTalentRepository = characterTalentRepository;
    }

    /**
     * Find CharacterTalent by ID.
     *
     * @param id CharacterTalent's primary key
     * @return CharacterTalent entity wrapped in Optional.
     */
    public Optional<CharacterTalent> findById(final Long id) {
        return characterTalentRepository.findById(id);
    }

    /**
     * Find CharacterTalent by Character.
     *
     * @param character Character
     * @return CharacterTalent entity wrapped in Optional.
     */
    public Optional<CharacterTalent> findByCharacter(final Character character) {
        val example = Example.of(CharacterTalent.builder().character(character).build());
        return characterTalentRepository.findOne(example);
    }

    /**
     * Saves a CharacterTalent to the database.
     *
     * @param characterTalentWrapper Character, User, and talent information
     * @return Newly created CharacterTalent.
     * @throws VanillaWowArmoryServiceException throws exception, if the user has not yet registered,
     *                                          or the character not exists.
     */
    public CharacterTalent save(final CharacterTalentWrapper characterTalentWrapper) throws VanillaWowArmoryServiceException {
        val optionalUser = userService.findByDiscordUserId(characterTalentWrapper.getDiscordUserId());
        if (optionalUser.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s hasn't registered yet, please use the $register command.", characterTalentWrapper.getDiscordUsername()));
        }
        val user = optionalUser.get();
        val optionalCharacter = characterService.findByUserAndName(user, characterTalentWrapper.getCharacterName());
        if (optionalCharacter.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s has no character named %s.", characterTalentWrapper.getDiscordUsername(), characterTalentWrapper.getCharacterName()));
        }
        val character = optionalCharacter.get();
        if (character.getCharacterTalent() != null) {
            characterTalentRepository.delete(character.getCharacterTalent());
        }
        return characterTalentRepository.saveAndFlush(new CharacterTalent(character, characterTalentWrapper));
    }
}
