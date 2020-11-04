package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.entity.DiscordUser;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.DiscordUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DiscordUserServiceTest {

    private static final Long USER_ID = 123L;
    private static final DiscordUserWrapper DISCORD_USER_WRAPPER = DiscordUserWrapper.builder()
            .id(USER_ID)
            .username("FANCY_USERNAME")
            .serverId(321L)
            .build();
    private static final DiscordUser DISCORD_USER = DiscordUser.builder()
            .userWrapper(DISCORD_USER_WRAPPER)
            .build();

    @Mock
    private DiscordUserRepository discordUserRepository;

    @InjectMocks
    private DiscordUserService discordUserService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        discordUserService = new DiscordUserService(discordUserRepository);
    }

    @Test
    public void testFindById() {
        when(discordUserRepository.findById(anyLong())).thenReturn(Optional.of(DISCORD_USER));

        discordUserService.findById(USER_ID);

        verify(discordUserRepository, times(1)).findById(USER_ID);
    }

    @Test
    public void testSaveNew() throws VanillaWowArmoryServiceException {
        when(discordUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        discordUserService.save(DISCORD_USER_WRAPPER);

        verify(discordUserRepository, times(1)).findById(USER_ID);
        verify(discordUserRepository, times(1)).saveAndFlush(any());
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testSaveAlreadyExisting() throws VanillaWowArmoryServiceException {
        when(discordUserRepository.findById(anyLong())).thenReturn(Optional.of(DISCORD_USER));

        discordUserService.save(DISCORD_USER_WRAPPER);

        verify(discordUserRepository, times(1)).findById(USER_ID);
        verify(discordUserRepository, times(0)).saveAndFlush(any());
    }

}