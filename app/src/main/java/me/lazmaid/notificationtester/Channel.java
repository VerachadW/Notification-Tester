package me.lazmaid.notificationtester;

public enum Channel {
    PUBLIC,
    PRIVATE,
    DIRECT;

    public String toCapitalizeName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public String getChannelId() {
        return name().toLowerCase() + "-channel";
    }
}
