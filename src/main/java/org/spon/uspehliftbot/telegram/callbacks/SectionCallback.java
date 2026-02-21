package org.spon.uspehliftbot.telegram.callbacks;

import io.github.natanimn.telebof.BotContext;
import io.github.natanimn.telebof.types.updates.CallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spon.uspehliftbot.entity.User;
import org.spon.uspehliftbot.repository.UserRepository;
import org.spon.uspehliftbot.telegram.LiftMessage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component("section")
public class SectionCallback implements Callback {

    private final UserRepository userRepository;
    private final LiftMessage liftMessage;

    @Override
    public void handleCallback(BotContext context, CallbackQuery callback) {
        context.answerCallbackQuery(callback.getId()).exec();
        // section1
        int userSection = Integer.parseInt(getArguments(callback).get(0));
        long chatId = callback.getMessage().getChat().getId();
        context.editMessageReplyMarkup(chatId, callback.getMessage().getMessageId())
                .replyMarkup(null).exec();

        context.sendMessage(chatId, "Секцію №" + userSection + " записано!")
                .exec();
        io.github.natanimn.telebof.types.chat_and_user.User telegramUser = callback.getFrom();
        userRepository.findByChatId(chatId).ifPresentOrElse(user -> {
            user.setUserSection(userSection);
            userRepository.save(user);
        }, () -> {
            User user = new User(
                    chatId,
                    telegramUser.getUsername(),
                    telegramUser.getFirstName() != null ? telegramUser.getFirstName() :
                            telegramUser.getLastName() != null ? " " + telegramUser.getLastName() : "",
                    userSection,
                    new Date(),
                    Collections.emptyList()
            );
            userRepository.save(user);
            log.info("NEW User has been registered: {} ({}), Section: {}",
                    user.getName(), user.getUserName(), user.getUserSection());
        });

        userRepository.findByChatId(chatId).ifPresent(user ->
                liftMessage.enterLiftMessage(context, user)
        );

    }
}
