package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            LOG.info("Starting Telegram bot");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            DefaultBotSession session = (DefaultBotSession) telegramBotsApi.registerBot(new Bot(getBotProperties()));
            LOG.info("Telegram Bot started");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private static Properties getBotProperties() {
        Properties properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("bot.properties");
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;

    }
}