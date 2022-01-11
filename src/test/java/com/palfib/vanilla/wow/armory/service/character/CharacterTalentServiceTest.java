package com.palfib.vanilla.wow.armory.service.character;

import com.palfib.vanilla.wow.armory.data.entity.ArmoryUser;
import com.palfib.vanilla.wow.armory.data.entity.Character;
import com.palfib.vanilla.wow.armory.data.entity.CharacterTalent;
import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterTalentWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.CharacterWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordUserWrapper;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import com.palfib.vanilla.wow.armory.repository.CharacterTalentRepository;
import com.palfib.vanilla.wow.armory.service.armoryuser.ArmoryUserService;
import com.palfib.vanilla.wow.armory.service.character.CharacterService;
import com.palfib.vanilla.wow.armory.service.character.CharacterTalentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CharacterTalentServiceTest {

    private static final String DISCORD_USER_ID = "123";
    private static final DiscordUserWrapper DISCORD_USER_WRAPPER = DiscordUserWrapper.builder()
            .discordUserId(DISCORD_USER_ID)
            .username("FANCY_USERNAME")
            .discordServerId("321")
            .build();
    private static final ArmoryUser ARMORY_USER = new ArmoryUser(DISCORD_USER_WRAPPER);

    private static final String CHARACTER_NAME = "FANCY_CHARACTERNAME";
    private static final CharacterWrapper CHARACTER_WRAPPER = CharacterWrapper.CharacterWrapperBuilder()
            .discordUserId(DISCORD_USER_ID)
            .characterName(CHARACTER_NAME)
            .race(Race.HUMAN)
            .characterClass(CharacterClass.WARRIOR)
            .level(60L)
            .build();
    private static final Character CHARACTER = new Character(ARMORY_USER, CHARACTER_WRAPPER);
    private static final Long CHARACTER_TALENT_ID = 345L;
    private static final String TALENT_NAME = "FANCY_TALENT_NAME";
    private final static CharacterTalentWrapper CHARACTER_TALENT_WRAPPER = CharacterTalentWrapper.CharacterTalentWrapperBuilder()
            .characterName(CHARACTER_NAME)
            .discordUserId(DISCORD_USER_ID)
            .name(TALENT_NAME)
            .talent("TALENT_URL")
            .build();
    private final static CharacterTalent CHARACTER_TALENT = new CharacterTalent(CHARACTER, CHARACTER_TALENT_WRAPPER);

    @Mock
    private ArmoryUserService userService;

    @Mock
    private CharacterService characterService;

    @Mock
    private CharacterTalentRepository characterTalentRepository;

    @InjectMocks
    private CharacterTalentService characterTalentService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        characterTalentService = new CharacterTalentService(userService, characterService, characterTalentRepository);
    }

    @Test
    public void testFindById() {
        when(characterTalentRepository.findById(anyLong())).thenReturn(Optional.of(CHARACTER_TALENT));

        characterTalentService.findById(CHARACTER_TALENT_ID);

        verify(characterTalentRepository, times(1)).findById(CHARACTER_TALENT_ID);
    }

    @Test
    public void testFindByCharacter() {
        when(characterTalentRepository.findOne(any(Example.class))).thenReturn(Optional.of(CHARACTER_TALENT));

        characterTalentService.findByCharacter(CHARACTER);

        verify(characterTalentRepository, times(1)).findOne(any(Example.class));
    }

    @Test
    public void testSaveCharacterTalentNew() throws VanillaWowArmoryServiceException {
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.of(ARMORY_USER));
        when(characterService.findByUserAndName(ARMORY_USER, CHARACTER_NAME)).thenReturn(Optional.of(CHARACTER));

        characterTalentService.save(CHARACTER_TALENT_WRAPPER);

        verify(characterTalentRepository, times(1)).saveAndFlush(CHARACTER_TALENT);
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testSaveCharacterTalentWithoutUser() throws VanillaWowArmoryServiceException {
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.empty());

        characterTalentService.save(CHARACTER_TALENT_WRAPPER);

        verify(characterTalentRepository, times(0)).saveAndFlush(any());
    }

    @Test(expected = VanillaWowArmoryServiceException.class)
    public void testSaveCharacterTalentWithoutCharacter() throws VanillaWowArmoryServiceException {
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.of(ARMORY_USER));
        when(characterService.findByUserAndName(ARMORY_USER, CHARACTER_NAME)).thenReturn(Optional.empty());

        characterTalentService.save(CHARACTER_TALENT_WRAPPER);

        verify(characterTalentRepository, times(0)).saveAndFlush(any());
    }

    @Test
    public void testSaveCharacterTalentAlreadyExisting() throws VanillaWowArmoryServiceException {
        CHARACTER.setCharacterTalent(CHARACTER_TALENT);
        when(userService.findByDiscordUserId(DISCORD_USER_ID)).thenReturn(Optional.of(ARMORY_USER));
        when(characterService.findByUserAndName(ARMORY_USER, CHARACTER_NAME)).thenReturn(Optional.of(CHARACTER));

        characterTalentService.save(CHARACTER_TALENT_WRAPPER);

        verify(characterTalentRepository, times(1)).delete(any());
        verify(characterTalentRepository, times(1)).saveAndFlush(any());
    }
}
