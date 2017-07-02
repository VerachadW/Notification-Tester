package me.lazmaid.notificationtester;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NotificationChannelProvider {
    private static NotificationChannelProvider instance;

    private Map<String, NotificationChannel> channels;

    private NotificationChannelProvider() {
        channels = new HashMap<>();
        channels.put("Public", createPublicGroupMessageChannel());
        channels.put("Private", createPrivateGroupMessageChannel());
        channels.put("Direct", createDirectMessageChannel());
    }

    public static NotificationChannelProvider getInstnace() {
        if (instance == null) {
            instance = new NotificationChannelProvider();
        }
        return instance;
    }

    public List<NotificationChannel> getChannels() {
        return new ArrayList<>(channels.values());
    }

    public String getChannelIdFromName(String name) {
        return channels.get(name).getId();
    }

    public String[] getChannelNames() {
        return channels.keySet().toArray(new String[channels.size()]);
    }

    private NotificationChannel createPublicGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel("Public", NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(false);
        return channel;
    }

    private NotificationChannel createDirectMessageChannel() {
        NotificationChannel channel = createNotificationChannel("Direct", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.RED);
        return channel;
    }

    private NotificationChannel createPrivateGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel("Private", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.BLUE);
        return channel;
    }

    private NotificationChannel createNotificationChannel(String name, int importance) {
        return new NotificationChannel(name.toLowerCase() + "-channel", name, importance);
    }
}
