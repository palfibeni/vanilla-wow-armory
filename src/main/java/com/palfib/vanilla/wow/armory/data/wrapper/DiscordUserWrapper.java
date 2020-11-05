package com.palfib.vanilla.wow.armory.data.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DiscordUserWrapper {
    private final String discordUserId;
    private final String username;
    private final String discordServerId;
}
