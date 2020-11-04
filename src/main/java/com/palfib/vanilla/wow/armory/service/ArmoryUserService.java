package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.ArmoryUserRepository;
import lombok.val;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Responsible for creating and managing ArmoryUser entities.
 */
@Component
public class ArmoryUserService extends AbstractService {

    private final ArmoryUserRepository userRepository;

    public ArmoryUserService(final ArmoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find ArmoryUser by ID.
     *
     * @param id ArmoryUser's primary key
     * @return ArmoryUser entity wrapped in Optional.
     */
    public Optional<ArmoryUser> findById(final Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find ArmoryUser by DiscordUserID.
     *
     * @param discordUserId ArmoryUser's Discord ID
     * @return ArmoryUser entity wrapped in Optional.
     */
    public Optional<ArmoryUser> findByDiscordUserId(final String discordUserId) {
        val example = Example.of(ArmoryUser.builder().discordUserId(discordUserId).build());
        return userRepository.findOne(example);
    }

    /**
     * Saves a new ArmoryUser to the database.
     *
     * @param userWrapper ArmoryUser information
     * @return Newly created ArmoryUser.
     * @throws VanillaWowArmoryServiceException throws exception, if the user already exists in the DB.
     */
    public ArmoryUser saveDiscordUser(final DiscordUserWrapper userWrapper) throws VanillaWowArmoryServiceException {
        val optionalDiscordUser = findByDiscordUserId(userWrapper.getDiscordUserId());
        if (optionalDiscordUser.isPresent()) {
            throw new VanillaWowArmoryServiceException(log, String.format("User is already registered %s.", userWrapper.getUsername()));
        }
        return userRepository.saveAndFlush(new ArmoryUser(userWrapper));
    }
}
