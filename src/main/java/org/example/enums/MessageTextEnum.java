package org.example.enums;

import org.telegram.telegrambots.meta.api.objects.MessageEntity;

public enum MessageTextEnum {

    START_MESSAGE(
    "<b>This is start message</b>\n\n" +
    "Next line for text"
    )


    ;

    private final String message;

    MessageTextEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
