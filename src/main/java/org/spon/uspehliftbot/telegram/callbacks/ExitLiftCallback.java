package org.spon.uspehliftbot.telegram.callbacks;

import io.github.natanimn.telebof.BotContext;
import io.github.natanimn.telebof.types.updates.CallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spon.uspehliftbot.ScheduledTasks;
import org.spon.uspehliftbot.entity.UserAction;
import org.spon.uspehliftbot.repository.UserRepository;
import org.spon.uspehliftbot.telegram.LiftMessage;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component("free")
public class ExitLiftCallback implements Callback {

    private final UserRepository userRepository;
    private final LiftMessage liftMessage;
    private final ScheduledTasks scheduledTasks;

    @Override
    public void handleCallback(BotContext context, CallbackQuery callback) {
        context.answerCallbackQuery(callback.getId()).exec();
        long chatId = callback.getMessage().getChat().getId();
        context.editMessageReplyMarkup(chatId, callback.getMessage().getMessageId())
                .replyMarkup(null).exec();

        userRepository.findByChatId(chatId).ifPresentOrElse(user -> {
                    UserAction userAction = UserAction.builder()
                            .user(user)
                            .action(UserAction.Action.EXIT)
                            .actionDate(new Date())
                            .isPassengerLift(null)
                            .build();
                    user.getUserActions().add(userAction);
                    userRepository.save(user);
                },
                () -> liftMessage.unknownUserMessage(context, chatId)
        );

        userRepository.findByChatId(chatId).ifPresent(user -> {
                    log.info("<--- {} ({}) has left elevator. Section: {}", user.getName(), user.getUserName(), user.getUserSection());
                    liftMessage.freedomMessage(context, user);
                    liftMessage.enterLiftMessage(context, user);
                    scheduledTasks.cancelSchedulingForUser(user);
                }
        );

    }
}
