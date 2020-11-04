package com.palfib.vanilla.wow.armory.data.wrapper;

import com.palfib.vanilla.wow.armory.data.enums.CharacterClass;
import com.palfib.vanilla.wow.armory.data.enums.Race;
import lombok.*;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterWrapper {
    private String discordUserId;
    private String discordUsername;
    private String name;
    private Long level;
    private Race race;
    private CharacterClass characterClass;
}
