package me.lazmaid.notificationtester.notification;

import android.app.NotificationChannel;

/**
 * Created by vwongsawangt on 7/3/2017 AD.
 */

public class NotificationChannelModel {
    private NotificationChannel channel;
    private int count;

    public NotificationChannelModel(NotificationChannel channel) {
        this.channel = channel;
        count = 0;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
