package com.palfib.vanilla.wow.armory.data.wrapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.function.Function;

@Getter
public class DiscordQuestionWrapper {
    private final String question;
    private final Function<String, String> validator;
    @Setter
    private String answer;

    @Builder
    public DiscordQuestionWrapper(final String question, final Function<String, String> validator) {
        this.question = question;
        this.validator = validator;
    }

    public boolean isFreeToAsk() {
        return StringUtils.isEmpty(answer);
    }
}
