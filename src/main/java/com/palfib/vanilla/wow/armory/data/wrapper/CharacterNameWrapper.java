package com.palfib.vanilla.wow.armory.data.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class CharacterNameWrapper {
    private String discordUserId;
    private String discordUsername;
    private String characterName;

    @Builder
    public CharacterNameWrapper(final String discordUserId, final String discordUsername, final String characterName) {
        this.discordUserId = discordUserId;
        this.discordUsername = discordUsername;
        this.characterName = characterName;
    }
}
