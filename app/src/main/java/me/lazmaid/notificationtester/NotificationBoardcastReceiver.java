package me.lazmaid.notificationtester;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by vwongsawangt on 7/3/2017 AD.
 */

public class NotificationBoardcastReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        String action = "";
        switch (intent.getIntExtra(Constants.REQUEST_CODE, 0)) {
            case Constants.SNOOZE_REQ_CODE:
                action = Constants.SNZOOE_ACTION;
                break;
            case Constants.BLOCK_REQ_CODE:
                action = Constants.BLOCK_ACTON;
                break;
            case Constants.REPLY_REQ_CODE:
                action = Constants.REPLY_ACTION;
                break;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getIntExtra(Constants.NOTIFICATION_ID, 0));

        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
    }
}
