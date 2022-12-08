package org.example.enums;

public enum AcceptedCommands {
    UNKNOWN_COMMAND((byte)-1, "unknown_command", ""),
    START((byte) 1, "start", "/start"),
//    STOP((byte) 2, "stop", "/stop"),
    ;

    private final byte id;
    private final String name;
    private final String command;
    AcceptedCommands(byte id, String name, String command) {
        this.id = id;
        this.name = name;
        this.command = command;
    }

    public static AcceptedCommands getByCommand(String command) {
        for (AcceptedCommands c : values()) {
            if (c.getCommand().equals(command)) {
                return c;
            }
        }
        return UNKNOWN_COMMAND;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }
}
