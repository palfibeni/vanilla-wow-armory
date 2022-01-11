package com.palfib.vanilla.wow.armory.data.wrapper;

import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
public class CharacterWrapper extends CharacterNameWrapper {
    private Long level;
    private Race race;
    private CharacterClass characterClass;

    @Builder(builderMethodName = "CharacterWrapperBuilder")
    public CharacterWrapper(final String discordUserId, final String discordUsername, final String characterName, final Long level, final Race race, final CharacterClass characterClass) {
        super(discordUserId, discordUsername, characterName);
        this.level = level;
        this.race = race;
        this.characterClass = characterClass;
    }
}
