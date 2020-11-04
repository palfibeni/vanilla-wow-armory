package com.palfib.vanilla.wow.armory.data.wrapper;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiscordUserWrapper {
    private final Long id;
    private final String username;
    private final Long serverId;
}
