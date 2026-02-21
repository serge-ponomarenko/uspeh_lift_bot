package org.spon.uspehliftbot.telegram.commands;

import io.github.natanimn.telebof.BotContext;
import io.github.natanimn.telebof.enums.ParseMode;
import io.github.natanimn.telebof.types.updates.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.spon.uspehliftbot.SettingsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("set")
@RequiredArgsConstructor
public class SetCommand implements Command {

    @Value("${telegram.admin-id}")
    private Long adminId;

    private final SettingsManager settingsManager;

    @Override
    @SneakyThrows
    public void runCommand(BotContext ctx, Message message) {
        long chatId = message.getChat().getId();
        if (chatId != adminId) return;

        List<String> arguments = getArguments(message);
        if (arguments.isEmpty()) {
            String userMessage = String.format("""
                            ⚙️ <b>Налаштування</b>
                            
                            /set chat 12345678951253
                            
                            alarm-chat-id (chat): %d
                            alarm-thread-id (thread): %d
                            
                            secondsToReminder (reminder): %d
                            secondsToAlarm (alarm): %d
                            
                            alarmPhone (phone): %s
                            """,
                    settingsManager.getAlarmChatId(),
                    settingsManager.getAlarmThreadId(),
                    settingsManager.getSecondsToReminder(),
                    settingsManager.getSecondsToAlarm(),
                    settingsManager.getAlarmPhone()
            );

            ctx.sendMessage(chatId, userMessage)
                    .parseMode(ParseMode.HTML)
                    .exec();
        } else {
            switch (arguments.get(0).toLowerCase()) {
                case "alarm-chat-id":
                case "chat":
                    long alarmChatId = Long.parseLong(arguments.get(1));
                    settingsManager.setAlarmChatId(alarmChatId);
                    break;
                case "alarm-thread-id":
                case "thread":
                    int alarmThreadId = Integer.parseInt(arguments.get(1));
                    settingsManager.setAlarmThreadId(alarmThreadId);
                    break;
                case "secondstoreminder":
                case "reminder":
                    int secondsToReminder = Integer.parseInt(arguments.get(1));
                    settingsManager.setSecondsToReminder(secondsToReminder);
                    break;
                case "secondstoalarm":
                case "alarm":
                    int secondsToAlarm = Integer.parseInt(arguments.get(1));
                    settingsManager.setSecondsToAlarm(secondsToAlarm);
                    break;
                case "alarmphone":
                case "phone":
                    String alarmPhone = arguments.get(1);
                    settingsManager.setAlarmPhone(alarmPhone);
                    break;
            }
            ctx.sendMessage(chatId, arguments.get(0) + " is set.")
                    .exec();
        }

    }

}
