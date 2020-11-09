package com.palfib.vanilla.wow.armory.data.wrapper;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterTalentWrapper {
    private String discordUserId;
    private String discordUsername;
    private String characterName;
    private String name;
    private String talent;
}
