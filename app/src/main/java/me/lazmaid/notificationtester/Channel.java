package me.lazmaid.notificationtester;

/**
 * Created by vwongsawangt on 7/6/2017 AD.
 */

public enum Channel {
    PUBLIC,
    PRIVATE,
    DIRECT;

    public String toCapitalizeName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
