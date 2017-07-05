package me.lazmaid.notificationtester.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lazmaid.notificationtester.Constants;
import me.lazmaid.notificationtester.NotificationBoardcastReceiver;
import me.lazmaid.notificationtester.R;

public final class NotificationProvider {
    private static NotificationProvider instance;

    private static final String PUBLIC_CHANNEL = "Public";
    private static final String PRIVATE_CHANNEL = "Private";
    private static final String DIRECT_CHANNEL = "Direct";

    private Map<String, NotificationChannel> channelModelMap;
    private Map<String, Integer> channelCountMap;

    private NotificationProvider() {
        channelModelMap = new HashMap<>();
        channelModelMap.put(PUBLIC_CHANNEL, createPublicGroupMessageChannel());
        channelModelMap.put(PRIVATE_CHANNEL, createPrivateGroupMessageChannel());
        channelModelMap.put(DIRECT_CHANNEL, createDirectMessageChannel());

        channelCountMap = new HashMap<>();
        channelCountMap.put(PUBLIC_CHANNEL, 0);
        channelCountMap.put(PRIVATE_CHANNEL, 0);
        channelCountMap.put(DIRECT_CHANNEL, 0);
    }

    public static NotificationProvider getInstnace() {
        if (instance == null) {
            instance = new NotificationProvider();
        }
        return instance;
    }

    public List<NotificationChannel> getChannels() {
        return new ArrayList<>(channelModelMap.values());
    }

    public String getChannelIdFromName(String name) {
        return channelModelMap.get(name).getId();
    }

    public String[] getChannelNames() {
        return channelModelMap.keySet().toArray(new String[channelModelMap.size()]);
    }

    private NotificationChannel createPublicGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel(PUBLIC_CHANNEL, NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(true);
        return channel;
    }

    private NotificationChannel createPrivateGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel(PRIVATE_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.BLUE);
        return channel;
    }

    private NotificationChannel createDirectMessageChannel() {
        NotificationChannel channel = createNotificationChannel(DIRECT_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.RED);
        channel.setBypassDnd(true);
        return channel;
    }

    private NotificationChannel createNotificationChannel(String name, int importance) {
        return new NotificationChannel(name.toLowerCase() + "-channel", name, importance);
    }

    public Notification buildNotification(Context context, String currentNotificationChannelName, String text) {
        Icon icon = Icon.createWithResource(context, R.mipmap.ic_launcher_round);
        Notification.Builder builder = new Notification.Builder(context, getChannelIdFromName(currentNotificationChannelName))
                .setContentTitle(currentNotificationChannelName)
                .setSmallIcon(icon)
                .setContentText(text);

        switch (currentNotificationChannelName) {
            case PUBLIC_CHANNEL:
            case PRIVATE_CHANNEL:
                builder.addAction(createAction(context, Constants.SNOOZE_REQ_CODE, R.drawable.ic_snooze_black_24dp,
                        Constants.SNZOOE_ACTION));
                break;
            case DIRECT_CHANNEL:
                builder.addAction(createAction(context, Constants.REPLY_REQ_CODE, R.drawable.ic_reply_black_24dp,
                        Constants.REPLY_ACTION));
                builder.addAction(createAction(context, Constants.BLOCK_REQ_CODE, R.drawable.ic_block_black_24dp, Constants.BLOCK_ACTON));
                break;
            default:
                throw new IllegalStateException("Not Support Channel name.");
        }

        int count = channelCountMap.get(currentNotificationChannelName);
        channelCountMap.put(currentNotificationChannelName, count + 1);

        int totalCount = getTotalNotificaitonCount();
        builder.setNumber(totalCount);
        Intent deleteIntent = new Intent(context, NotificationBoardcastReceiver.class);
        deleteIntent.putExtra(Constants.REQUEST_CODE, Constants.DELETE_NOTIFICATION_REQ_CODE);
        deleteIntent.putExtra(Constants.CHANNEL_NAME, currentNotificationChannelName);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, Constants.DELETE_NOTIFICATION_REQ_CODE, deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setDeleteIntent(deletePendingIntent);
        return builder.build();
    }

    private Notification.Action createAction(Context context, int requestCode, @DrawableRes int iconId, String action) {
        Intent intent = new Intent(context, NotificationBoardcastReceiver.class);
        intent.setAction(action);
        intent.putExtra(Constants.REQUEST_CODE, requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Icon icon = Icon.createWithResource(context, iconId);
        return new Notification.Action.Builder(icon, action, pendingIntent)
                .build();
    }

    public void decreaseNotificationCount(String channelName) {
        int count = channelCountMap.get(channelName);
        channelCountMap.put(channelName, count - 1);
    }

    public int getTotalNotificaitonCount() {
        int count = 0;
        for (int num: channelCountMap.values()) {
            count += num;
        }
        return count;
    }
}
