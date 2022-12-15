package org.example.enums;

public enum RtuDialogStateEnum  {
    NOTHING((byte) 0, "No state"),
    SEMESTER((byte) 1, "Select semester"),
    FACULTY((byte) 2, "Select faculty"),
    PROGRAM((byte) 3, "Select program"),
    COURSE((byte) 4, "Select course"),
    GROUP((byte) 5, "Select group")
    ;

    private final byte id;
    private final String text;

    RtuDialogStateEnum(byte id, String text) {
        this.id = id;
        this.text = text;
    }

    public byte getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
