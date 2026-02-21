package org.spon.uspehliftbot.telegram;

import io.github.natanimn.telebof.BotContext;
import io.github.natanimn.telebof.enums.ParseMode;
import io.github.natanimn.telebof.types.keyboard.InlineKeyboardButton;
import io.github.natanimn.telebof.types.keyboard.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spon.uspehliftbot.SettingsManager;
import org.spon.uspehliftbot.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiftMessage {

    private final SettingsManager settingsManager;

    public void enterLiftMessage(BotContext context, User user) {
        String userMessage = String.format("""
                \uD83E\uDD1E <b>Заходиш у ліфт?</b>
                
                Сподіваюсь, цього разу без пригод 😅🛗
                
                Твоя секція <b>№%s</b>
                
                <i>Обери, в який саме:</i> 👇
                """, user.getUserSection());

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addKeyboard(
                new InlineKeyboardButton("\uD83D\uDFE5 Пасажирський", "enter_Pass"),
                new InlineKeyboardButton("\uD83D\uDFE9 Вантажний", "enter_Cargo")
        );

        context.sendMessage(user.getChatId(), userMessage)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .exec();
    }

    public void exitLiftMessage(BotContext context, User user) {
        String userMessage = """
                😮‍💨 <b>Дуже сміливо!</b>
                
                Ліфт закрив двері... поїхали 🛗
                
                Як тільки будеш на свободі —
                натисни кнопку нижче 👇
                
                <i>Через 5 хвилин я перевірю, чи все ок.</i>
                Якщо ти не відповіси —
                я підніму тривогу в загальній групі 🚨
                (і тебе підуть визволяти... сподіваюсь 😅)
                """;

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addKeyboard(
                new InlineKeyboardButton("\uD83E\uDD29 На свободі!", "free")
        );

        context.sendMessage(user.getChatId(), userMessage)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .exec();
    }

    public void reminderMessage(BotContext context, User user) {
        String userMessage = """
                🤨 <b>Може таки натиснеш кнопочку?</b>
                
                Я вже починаю нервово дивитися на таймер ⏳
                
                <i>Через 1 хвилину</i> я підніму тривогу в усіх групах 🚨
                І тоді буде справжня операція "Визволення" 🛗😅
                """;

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addKeyboard(
                new InlineKeyboardButton("\uD83E\uDD29 На свободі!", "free")
        );

        context.sendMessage(user.getChatId(), userMessage)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .exec();
    }

    public void freedomMessage(BotContext context, User user) {
        String userMessage = """
                😍 <b>Фууух! Я переживав!</b>
                
                Радий, що ліфт цього разу відпустив тебе 😄🛗
                
                До нових пригод!
                (але бажано без режиму "рятувальна операція") 🚨😅
                """;

        context.sendMessage(user.getChatId(), userMessage)
                .parseMode(ParseMode.HTML)
                .exec();
    }

    public void alarmMessage(BotContext context, User user, boolean isPassengerLift) {
        String userMessage = """
                🚨🚨🚨 <b>Все! Викликаю спецназ ліфтових військ!</b>
                
                Сусіди вже натягують капці
                і готуються до рятувальної операції 🏃‍♂️🛗
                
                Коли тебе визволять —
                натисни кнопочку внизу ☺️👇
                """;

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addKeyboard(
                new InlineKeyboardButton("\uD83E\uDD29 На свободі!", "free")
        );

        context.sendMessage(user.getChatId(), userMessage)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .exec();

        String alarmMessage = String.format("""
                        🚨🚨🚨 <b>МОЖЛИВО ЗАСТРЯГЛА ЛЮДИНА!</b> 🚨🚨🚨
                        
                        🏢 <b>Секція:</b> %d
                        🛗 <b>Ліфт:</b> %s
                        👤 <b>Користувач:</b> %s (@%s)
                        
                        Телефон диспетчера: %s
                        
                        Будь ласка, перевірте ситуацію.
                        """,
                user.getUserSection(), isPassengerLift ? "Пасажирський" : "Вантажний",
                user.getName(), user.getUserName(), settingsManager.getAlarmPhone()
        );

        context.sendMessage(settingsManager.getAlarmChatId(), alarmMessage)
                .parseMode(ParseMode.HTML)
                .messageThreadId(settingsManager.getAlarmThreadId())
                .exec();

        log.info("!!! {} ({}) has been stuck! Section: {}, {}",
                user.getName(), user.getUserName(), user.getUserSection(), isPassengerLift ? "Passenger" : "Cargo");

    }

    public void unknownUserMessage(BotContext context, Long chatId) {
        context.sendMessage(chatId, """
                            😅 <b>Упс!</b>
                            
                            Схоже, ми ще не знайомі.
                            
                            Натисни /start — і я швидко проведу тебе через реєстрацію 🛗✨
                            """)
                .parseMode(ParseMode.HTML)
                .exec();
    }
}
