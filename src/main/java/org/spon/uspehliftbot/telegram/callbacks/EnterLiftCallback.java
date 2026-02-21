package org.spon.uspehliftbot.telegram.callbacks;

import io.github.natanimn.telebof.BotContext;
import io.github.natanimn.telebof.types.updates.CallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spon.uspehliftbot.ScheduledTasks;
import org.spon.uspehliftbot.SettingsManager;
import org.spon.uspehliftbot.entity.UserAction;
import org.spon.uspehliftbot.repository.UserRepository;
import org.spon.uspehliftbot.telegram.LiftMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component("enter")
public class EnterLiftCallback implements Callback {

    private final UserRepository userRepository;
    private final LiftMessage liftMessage;
    private final ScheduledTasks scheduledTasks;
    private final SettingsManager settingsManager;

    @Override
    public void handleCallback(BotContext context, CallbackQuery callback) {
        context.answerCallbackQuery(callback.getId()).exec();

        boolean isPassengerLift = getArguments(callback).get(0).equals("Pass");
        long chatId = callback.getMessage().getChat().getId();
        context.editMessageReplyMarkup(chatId, callback.getMessage().getMessageId())
                .replyMarkup(null).exec();

        io.github.natanimn.telebof.types.chat_and_user.User telegramUser = callback.getFrom();
        userRepository.findByChatId(chatId).ifPresentOrElse(user -> {
                    UserAction userAction = UserAction.builder()
                            .user(user)
                            .action(UserAction.Action.ENTER)
                            .actionDate(new Date())
                            .isPassengerLift(isPassengerLift)
                            .build();
                    user.getUserActions().add(userAction);
                    userRepository.save(user);

                },
                () -> liftMessage.unknownUserMessage(context, chatId)
        );

        userRepository.findByChatId(chatId).ifPresent(user -> {
                    log.info("---> {} ({}) has entered elevator. Section: {}, {}",
                            user.getName(), user.getUserName(), user.getUserSection(), isPassengerLift ? "Passenger" : "Cargo");
                    liftMessage.exitLiftMessage(context, user);
                    scheduledTasks.scheduling(
                            () -> liftMessage.reminderMessage(context, user),
                            Instant.now().plus(settingsManager.getSecondsToReminder(), ChronoUnit.SECONDS),
                            user
                    );
                    scheduledTasks.scheduling(
                            () -> liftMessage.alarmMessage(context, user, isPassengerLift),
                            Instant.now().plus(settingsManager.getSecondsToAlarm(), ChronoUnit.SECONDS),
                            user
                    );
                }
        );

    }
}
