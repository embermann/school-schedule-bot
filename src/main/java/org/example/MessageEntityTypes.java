package org.example;

public enum MessageEntityTypes {
    NOT_FOUND                                                               ((byte)-1, "not_found", "not_found"),
    MENTION                                                                 ((byte) 1, "mention", "@username"),
    HASHTAG                                                                 ((byte) 2, "hashtag", "@username"),
    CASHTAG                                                                 ((byte) 3, "cashtag", "@username"),
    BOT_COMMAND                                                             ((byte) 4, "bot_command", "@username"),
    URL                                                                     ((byte) 5, "url", "@username"),
    EMAIL                                                                   ((byte) 6, "email", "@username"),
    PHONE_NUMBER                                                            ((byte) 7, "phone_number", "@username"),
    BOLD                                                                    ((byte) 8, "bold", "@username"),
    ITALIC                                                                  ((byte) 9, "italic", "@username"),
    UNDERLINE                                                               ((byte) 10, "underline", "@username"),
    STRIKETHROUGH                                                           ((byte) 11, "strikethrough", "@username"),
    SPOILER                                                                 ((byte) 12, "spoiler", "@username"),
    CODE                                                                    ((byte) 13, "code", "@username"),
    PRE                                                                     ((byte) 14, "pre", "@username"),
    TEXT_LINK                                                               ((byte) 15, "text_link", "@username"),
    TEXT_MENTION                                                            ((byte) 16, "text_mention", "@username"),
    CUSTOM_EMOJI                                                            ((byte) 17, "custom_emoji", "@username"),
    ;

    private final byte id;
    private final String name;
    private final String example;

    MessageEntityTypes(byte id, String name, String example) {
        this.id = id;
        this.name = name;
        this.example = example;
    }

    public static MessageEntityTypes getByName(String type) {
        for (MessageEntityTypes t : values()) {
            if (t.getName().equals(type)) {
                return t;
            }
        }
        return NOT_FOUND;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getExample() {
        return example;
    }
}
