package org.spon.uspehliftbot.telegram.commands;

import io.github.natanimn.telebof.BotContext;
import io.github.natanimn.telebof.enums.ParseMode;
import io.github.natanimn.telebof.requests.send.SendMessage;
import io.github.natanimn.telebof.types.keyboard.InlineKeyboardButton;
import io.github.natanimn.telebof.types.keyboard.InlineKeyboardMarkup;
import io.github.natanimn.telebof.types.updates.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.spon.uspehliftbot.telegram.LiftMessage;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("start")
@RequiredArgsConstructor
public class StartCommand implements Command {

    @Override
    @SneakyThrows
    public void runCommand(BotContext ctx, Message message) {
        String userMessage = "\uD83D\uDC4B <b>Привіт!</b>\n\n" +
                "Якщо ліфт вирішив застрягнути — я допоможу тобі (надіюсь) повідомити про це 🚨🛗\n\n" +
                "<i>Для початку обери свою секцію</i> 👇";

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addKeyboard(
                new InlineKeyboardButton("1", "section_1"),
                new InlineKeyboardButton("2", "section_2"),
                new InlineKeyboardButton("3", "section_3")
        );
        keyboard.addKeyboard(
                new InlineKeyboardButton("4", "section_4"),
                new InlineKeyboardButton("5", "section_5"),
                new InlineKeyboardButton("6", "section_6")
        );
        keyboard.addKeyboard(
                new InlineKeyboardButton("7", "section_7"),
                new InlineKeyboardButton("8", "section_8"),
                new InlineKeyboardButton("9", "section_9")
        );

        ctx.sendMessage(message.getChat().getId(), userMessage)
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard)
                .exec();
    }

}
