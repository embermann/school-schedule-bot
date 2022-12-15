package org.example.enums;

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
