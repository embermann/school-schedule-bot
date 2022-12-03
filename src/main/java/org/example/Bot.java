package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bot extends TelegramLongPollingBot {

    Logger LOG = LoggerFactory.getLogger(Bot.class);

    private Set<Long> chats;

    public Bot() {
        this.chats = new HashSet<>();
    }

    @Override
    public String getBotUsername() {
        return "@SchoolScheduleLVBot";
    }

    @Override
    public String getBotToken() {
        return "2020804969:AAFzR5Ktrf138p8nBpTpKw4S1t1rBZVpbeo";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();

        LOG.info("Update received from chat id: ", chatId);

        processUpdate(update, chatId);
        String messageText = update.getMessage().getText();
        if (chats.contains(chatId)) {
//            sendSameReply(messageText, chatId);
        } else {
            chats.add(chatId);
            sendGreetingsReply(chatId);
        }
    }

    private void processUpdate(Update update, Long chatId) {
        if (update.getMessage().hasEntities()) {
            checkEntities(update.getMessage().getEntities(), chatId);
        }

    }

    private void checkEntities(List<MessageEntity> entities, Long chatId) {
        for (MessageEntity entity : entities) {
            switch (MessageEntityTypes.getByName(entity.getType())) {
                case BOT_COMMAND:
                    processCommand(entity, chatId);
                    break;

                case NOT_FOUND: // == default
            }

        }
    }

    private void processCommand(MessageEntity entity, Long chatId) {
        switch (AcceptedCommands.getByCommand(entity.getText())) {
            case START:
                sendStartCommandReply(chatId);
                break;
            case STOP:
                // stop
                break;
            case UNKNOWN_COMMAND: // == default
        }

    }

    private void sendSameReply(String messageText, Long chatId) {
        try {
            execute(new SendMessage(chatId.toString(), messageText));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendGreetingsReply(Long chatId) {
        try {
            execute(new SendMessage(chatId.toString(), "Hello to new chat"));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStartCommandReply(Long chatId) {
        try {
            execute(new SendMessage(chatId.toString(), "Hello! You started me, the best bot ever!!!"));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void onStopCommand(Long chatId) {
        // stop bot
    }

}
