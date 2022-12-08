package org.example;

import org.example.enums.AcceptedCommands;
import org.example.enums.MessageEntityTypes;
import org.example.enums.MessageTextEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {

    Logger LOG =  LoggerFactory.getLogger(Bot.class);

    private final Properties properties;
    // Selects parsing mode for sent messages
    private final String MESSAGE_PARSE_MODE = "HTML";

    public Bot(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getBotUsername() {
        return properties.getProperty("username");
    }

    @Override
    public String getBotToken() {
        return properties.getProperty("token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOG.info("Update received: {}", update.getUpdateId());

        processUpdate(update);
    }

    private void processUpdate(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            Message message = update.getMessage();
            LOG.info("Message received: {} From chat: {}", message.getMessageId(), chatId);

            if (message.hasEntities()) {
                LOG.info("Message has entities in chat: {}", chatId);
                checkEntities(message.getEntities(), chatId);
            }

        }
    }

    private void checkEntities(List<MessageEntity> entities, Long chatId) {
        for (MessageEntity entity : entities) {
            switch (MessageEntityTypes.getByName(entity.getType())) {
                case BOT_COMMAND:
                    LOG.info("Found {} in chat: {}", MessageEntityTypes.BOT_COMMAND.getName(), chatId);
                    processCommand(entity, chatId);
                    break;

                case NOT_FOUND: // == default
            }

        }
    }

    private void processCommand(MessageEntity entity, Long chatId) {
        switch (AcceptedCommands.getByCommand(entity.getText())) {
            case START:
                LOG.info("Found {} command in chat: {}", AcceptedCommands.START.getName(), chatId);
                sendStartCommandReply(chatId);
                break;

            case UNKNOWN_COMMAND: // == default
        }

    }

    private void sendStartCommandReply(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(MessageTextEnum.START_MESSAGE.getMessage())
                .parseMode(MESSAGE_PARSE_MODE)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
