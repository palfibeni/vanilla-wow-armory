package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.entity.Character;
import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.CharacterRepository;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Responsible for creating and managing Character entities.
 */
@Component
public class CharacterService extends AbstractService {

    private final ArmoryUserService userService;
    private final CharacterRepository characterRepository;

    public CharacterService(final ArmoryUserService userService, final CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
        this.userService = userService;
    }

    /**
     * Find Character by ID.
     *
     * @param id Character's primary key
     * @return Character entity wrapped in Optional.
     */
    public Optional<Character> findById(final Long id) {
        return characterRepository.findById(id);
    }

    /**
     * Find Character by user.
     *
     * @param armoryUser User
     * @return User's characters.
     */
    public List<Character> listByUser(final ArmoryUser armoryUser) {
        val example = Example.of(Character.builder().armoryUser(armoryUser).build());
        return characterRepository.findAll(example);
    }

    /**
     * Find Character by user, and name.
     *
     * @param armoryUser User
     * @param name Character's name
     * @return Character entity wrapped in Optional.
     */
    public Optional<Character> findByUserAndName(final ArmoryUser armoryUser, final String name) {
        val example = Example.of(Character.builder().armoryUser(armoryUser).name(name).build());
        return characterRepository.findOne(example);
    }

    /**
     * Saves a new Character to the database.
     *
     * @param characterWrapper Character and User information
     * @return Newly created Character.
     * @throws VanillaWowArmoryServiceException throws exception, if the user has not yet registered,
     *                                          or the character already exists in the DB.
     */
    public Character save(final CharacterWrapper characterWrapper) throws VanillaWowArmoryServiceException {
        val optionalUser = userService.findByDiscordUserId(characterWrapper.getDiscordUserId());
        if (optionalUser.isEmpty()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s hasn't registered yet, please use the $register command.", characterWrapper.getDiscordUsername()));
        }
        val user = optionalUser.get();
        val optionalCharacter = findByUserAndName(user, characterWrapper.getName());
        if (optionalCharacter.isPresent()) {
            throw new VanillaWowArmoryServiceException(log, String.format("%s user already has %s named character.", characterWrapper.getDiscordUsername(), characterWrapper.getName()));
        }
        return characterRepository.saveAndFlush(new Character(user, characterWrapper));
    }
}
