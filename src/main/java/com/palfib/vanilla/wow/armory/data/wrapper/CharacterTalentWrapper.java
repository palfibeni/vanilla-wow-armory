package com.palfib.vanilla.wow.armory.data.wrapper;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class CharacterTalentWrapper extends CharacterNameWrapper {
    private String name;
    private String talent;

    @Builder(builderMethodName = "CharacterTalentWrapperBuilder")
    public CharacterTalentWrapper(final String discordUserId, final String discordUsername, final String characterName, final String name, final String talent) {
        super(discordUserId, discordUsername, characterName);
        this.name = name;
        this.talent = talent;
    }
}
