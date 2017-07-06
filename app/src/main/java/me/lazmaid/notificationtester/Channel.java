package me.lazmaid.notificationtester;

public enum Channel {
    PUBLIC("Public"),
    PRIVATE("Private"),
    DIRECT("Direct");

    private final String displayName;

    Channel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChannelId() {
        return name().toLowerCase() + "-channel";
    }
}
