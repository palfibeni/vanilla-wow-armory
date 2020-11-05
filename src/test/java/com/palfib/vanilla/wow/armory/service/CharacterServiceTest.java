package com.palfib.vanilla.wow.armory.service;

import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.data.entity.Character;
import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.CharacterRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CharacterServiceTest {

    private static final String DISCORD_USER_ID = "123";
    private static final DiscordUserWrapper DISCORD_USER_WRAPPER = DiscordUserWrapper.builder()
            .discordUserId(DISCORD_USER_ID)
            .username("FANCY_USERNAME")
            .discordServerId("321")
            .build();
    private static final ArmoryUser ARMORY_USER = new ArmoryUser(DISCORD_USER_WRAPPER);

    private static final Long CHARACTER_ID = 234L;
    private static final String CHARACTER_NAME = "FANCY_CHARACTERNAME";
    private static final CharacterWrapper CHARACTER_WRAPPER = CharacterWrapper.builder()
            .discordUserId(DISCORD_USER_ID)
            .name(CHARACTER_NAME)
            .race(Race.HUMAN)
            .characterClass(CharacterClass.WARRIOR)
            .level(60L)
            .build();
    private static final Character CHARACTER = new Character(ARMORY_USER, CHARACTER_WRAPPER);

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private ArmoryUserService userService;

    @InjectMocks
    private CharacterService characterService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        characterService = new CharacterService(userService, characterRepository);
    }

    @Test
    public void testFindById() {
        when(characterRepository.findById(anyLong())).thenReturn(Optional.of(CHARACTER));

        characterService.findById(CHARACTER_ID);

        verify(characterRepository, times(1)).findById(CHARACTER_ID);
    }

    @Test
    public void testListByUser() {
        when(characterRepository.findAll(any(Example.class))).thenReturn(List.of(CHARACTER));

        characterService.listByUser(ARMORY_USER);

        verify(characterRepository, times(1)).findAll(any(Example.class));
    }

    @Test
    public void testFindByUserAndName() {
        when(characterRepository.findOne(any())).thenReturn(Optional.of(CHARACTER));

        characterService.findByUserAndName(ARMORY_USER, CHARACTER_NAME);

        verify(characterRepository, times(1)).findOne(any());
    }

    @Test
    public void testSaveCharacterNew() throws VanillaWowArmoryServiceException {
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.of(ARMORY_USER));
        when(characterService.findByUserAndName(ARMORY_USER, CHARACTER_NAME)).thenReturn(Optional.empty());

        characterService.save(CHARACTER_WRAPPER);

        verify(characterRepository, times(1)).saveAndFlush(CHARACTER);
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testSaveCharacterWithoutUser() throws VanillaWowArmoryServiceException {
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.empty());

        characterService.save(CHARACTER_WRAPPER);

        verify(characterRepository, times(0)).saveAndFlush(any());
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testSaveCharacterAlreadyExisting() throws VanillaWowArmoryServiceException {
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.of(ARMORY_USER));
        when(characterService.findByUserAndName(ARMORY_USER, CHARACTER_NAME)).thenReturn(Optional.of(CHARACTER));

        characterService.save(CHARACTER_WRAPPER);

        verify(characterRepository, times(0)).saveAndFlush(any());
    }
}