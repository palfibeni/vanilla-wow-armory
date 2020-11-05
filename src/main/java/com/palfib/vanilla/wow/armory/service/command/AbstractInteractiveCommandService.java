package com.palfib.vanilla.wow.armory.service.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionSequenceWrapper;
import com.palfib.vanilla.wow.armory.data.wrapper.DiscordQuestionWrapper;
import com.palfib.vanilla.wow.armory.exception.AbstractVanillaWowArmoryException;
import com.palfib.vanilla.wow.armory.exception.VanillaWowArmoryServiceException;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class AbstractInteractiveCommandService extends AbstractCommandService {

    private final String TIME_OUT_MESSAGE = "Sorry, you took too long.";

    /**
     * Generates a Command for the CommandClientBuilder
     *
     * @return jdautilities Command ready to use.
     */
    public Command generateCommand(final EventWaiter eventWaiter) {
        return new CommandBuilder().setName(getCommandName())
                .setBotPermissions(getPermissions())
                .setGuildOnly(isGuildOnly())
                .setAliases(getAliases())
                .build(event -> handleCommandExecution(event, eventWaiter));
    }

    /**
     * Executes the Command.
     *
     * @param event input Command parameters from the user.
     */
    protected void handleCommandExecution(final CommandEvent event, final EventWaiter eventWaiter) {
        try {
            logUserEntrance(event);
            validateArguments(event);
            executeCommand(event, eventWaiter);
        } catch (AbstractVanillaWowArmoryException ex) {
            event.replyWarning(ex.getMessage());
        }
    }

    protected abstract void executeCommand(final CommandEvent event, final EventWaiter eventWaiter) throws VanillaWowArmoryServiceException;

    /**
     * Asks questions from the User, til all of them are answered
     * or, takes more than 10 second to reply.
     *
     * @param questionSequenceWrapper Questions' data wrapped with event details, and eventWaiter.
     * @param answerConsumer          Consumer which will handle the answers.
     */
    protected void askQuestionSequence(final DiscordQuestionSequenceWrapper questionSequenceWrapper,
                                       final Consumer<DiscordQuestionSequenceWrapper> answerConsumer) {
        val optQuestionWrapper = questionSequenceWrapper.getQuestions().values().stream()
                .filter(DiscordQuestionWrapper::isFreeToAsk)
                .findFirst();
        if (optQuestionWrapper.isPresent()) {
            askQuestion(optQuestionWrapper.get(), questionSequenceWrapper, answerConsumer);
        } else {
            answerConsumer.accept(questionSequenceWrapper);
        }
    }

    /**
     * Ask a question to the User, and handles the response.
     *
     * @param questionWrapper         The current question's data
     * @param questionSequenceWrapper Questions' data wrapped with event details, and eventWaiter.
     * @param answerConsumer          Consumer which will handle the answers.
     */
    private void askQuestion(final DiscordQuestionWrapper questionWrapper,
                             final DiscordQuestionSequenceWrapper questionSequenceWrapper,
                             final Consumer<DiscordQuestionSequenceWrapper> answerConsumer) {
        val event = questionSequenceWrapper.getEvent();
        event.reply(questionWrapper.getQuestion());
        questionSequenceWrapper.getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                messageEvent -> isMessageRelevant(event, messageEvent),
                messageEvent -> handleUserAnswer(event, questionWrapper, messageEvent, questionSequenceWrapper, answerConsumer),
                10, TimeUnit.SECONDS, () -> event.reply(TIME_OUT_MESSAGE));
    }

    /**
     * Validates the answer:
     * if there is an error, we ask the same question once again,
     * otherwise continues to the next question, or the answerConsumer.
     *
     * @param event                   Original command event
     * @param questionWrapper         The current question's data
     * @param messageEvent            New message event
     * @param questionSequenceWrapper Questions' data wrapped with event details, and eventWaiter.
     * @param answerConsumer          Consumer which will handle the answers.
     */
    private void handleUserAnswer(final CommandEvent event, final DiscordQuestionWrapper questionWrapper,
                                  final MessageReceivedEvent messageEvent, final DiscordQuestionSequenceWrapper questionSequenceWrapper,
                                  final Consumer<DiscordQuestionSequenceWrapper> answerConsumer) {
        val answer = messageEvent.getMessage().getContentRaw().trim().replaceAll("\\s+", " ");
        val validationError = questionWrapper.getValidator().apply(answer);
        if (StringUtils.isEmpty(validationError)) {
            questionWrapper.setAnswer(answer);
            askQuestionSequence(questionSequenceWrapper, answerConsumer);
        } else {
            event.reply(validationError);
            askQuestion(questionWrapper, questionSequenceWrapper, answerConsumer);
        }
    }

    /**
     * Making sure it's by the same user, and in the same channel, and for safety, a different message.
     *
     * @param event        Original command event
     * @param messageEvent New message event
     * @return True if the new message is coming from the same user.
     */
    private boolean isMessageRelevant(final CommandEvent event, final MessageReceivedEvent messageEvent) {
        return messageEvent.getAuthor().equals(event.getAuthor())
                && messageEvent.getChannel().equals(event.getChannel())
                && !messageEvent.getMessage().equals(event.getMessage());
    }
}
