package me.lazmaid.notificationtester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.lazmaid.notificationtester.notification.NotificationProvider;

/**
 * Created by vwongsawangt on 7/3/2017 AD.
 */

public class NotificationBoardcastReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        if (intent.getIntExtra(Constants.REQUEST_CODE, 0) == Constants.DELETE_NOTIFICATION_REQ_CODE) {
            String channelName = intent.getStringExtra(Constants.CHANNEL_NAME);
            NotificationProvider.getInstnace().decreaseNotificationCount(channelName);
        }
    }
}
