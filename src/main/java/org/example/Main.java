package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        Logger LOG = LoggerFactory.getLogger(Main.class);
        try {
            LOG.info("Starting Telegram bot");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new Bot());
            LOG.info("Telegram Bot started");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}