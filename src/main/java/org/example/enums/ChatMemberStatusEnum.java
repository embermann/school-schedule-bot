package org.example.enums;

public enum ChatMemberStatusEnum {

    NOT_FOUND((byte) -1, "not_found"),
    OWNER((byte) 1, "creator"),
    ADMINISTRATOR((byte) 2, "administrator"),
    MEMBER((byte) 3, "member"),
    RESTRICTED((byte) 4, "restricted"),
    LEFT((byte) 5, "left"),
    BANNED((byte) 6, "kicked")
    ;


    private final byte id;
    private final String name;

    ChatMemberStatusEnum(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ChatMemberStatusEnum getByName(String name) {
        for (ChatMemberStatusEnum var : values()) {
            if (var.getName().equals(name)) {
                return var;
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
}
