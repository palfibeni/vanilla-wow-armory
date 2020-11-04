package com.palfib.vanilla.wow.armory.data.wrapper;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DiscordQuestionSequenceWrapper {
    private final CommandEvent event;
    private final EventWaiter eventWaiter;
    private final Map<String, DiscordQuestionWrapper> questions;
}
