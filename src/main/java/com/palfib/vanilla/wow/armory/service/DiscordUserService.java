package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.entity.DiscordUser;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.DiscordUserRepository;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Responsible for creating and managing DiscordUser entities.
 */
@Component
public class DiscordUserService extends AbstractService {

    private final DiscordUserRepository discordUserRepository;

    public DiscordUserService(final DiscordUserRepository discordUserRepository) {
        this.discordUserRepository = discordUserRepository;
    }

    /**
     * Find DiscordUser by ID.
     *
     * @param id primary key
     * @return DiscordUser entity wrapped in Optional.
     */
    public Optional<DiscordUser> findById(final Long id) {
        return discordUserRepository.findById(id);
    }

    /**
     * Saves a new DiscordUser to the database.
     *
     * @param userWrapper User information
     * @return Newly created DiscordUser.
     * @throws VanillaWowArmoryServiceException throws exception, if the user already exists in the DB.
     */
    public DiscordUser save(final DiscordUserWrapper userWrapper) throws VanillaWowArmoryServiceException {
        val optionalDiscordUser = findById(userWrapper.getId());
        if (optionalDiscordUser.isPresent()) {
            throw new VanillaWowArmoryServiceException(log, String.format("User is already registered with ID %d.", userWrapper.getId()));
        }
        return discordUserRepository.save(DiscordUser.builder().userWrapper(userWrapper).build());
    }
}
