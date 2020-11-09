package com.palfib.vanilla.wow.armory.service.armoryuser;

import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.ArmoryUserRepository;
import com.palfib.vanilla.wow.armory.service.armoryuser.ArmoryUserService;
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
public class ArmoryUserServiceTest {

    private static final Long USER_ID = 123L;
    private static final String DISCORD_USER_ID = "123";
    private static final DiscordUserWrapper DISCORD_USER_WRAPPER = DiscordUserWrapper.builder()
            .discordUserId(DISCORD_USER_ID)
            .username("FANCY_USERNAME")
            .discordServerId("321")
            .build();
    private static final ArmoryUser ARMORY_USER = new ArmoryUser(DISCORD_USER_WRAPPER);

    @Mock
    private ArmoryUserRepository userRepository;

    @InjectMocks
    private ArmoryUserService userService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new ArmoryUserService(userRepository);
    }

    @Test
    public void testFindById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ARMORY_USER));

        userService.findById(USER_ID);

        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    public void testFindByDiscordUserId() {
        when(userRepository.findOne(any())).thenReturn(Optional.of(ARMORY_USER));

        userService.findByDiscordUserId(DISCORD_USER_ID);

        verify(userRepository, times(1)).findOne(any());
    }

    @Test
    public void testSaveDiscordUserNew() throws VanillaWowArmoryServiceException {
        when(userRepository.findOne(any())).thenReturn(Optional.empty());

        userService.saveDiscordUser(DISCORD_USER_WRAPPER);

        verify(userRepository, times(1)).saveAndFlush(ARMORY_USER);
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testSaveDiscordUserAlreadyExisting() throws VanillaWowArmoryServiceException {
        when(userRepository.findOne(any())).thenReturn(Optional.of(ARMORY_USER));

        userService.saveDiscordUser(DISCORD_USER_WRAPPER);

        verify(userRepository, times(0)).saveAndFlush(any());
    }

}