package me.lazmaid.notificationtester;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText messageView;

    private NotificationManager notificationManager;
    private String currentNotificationChannelName;
    private int notificationCount;

    private String[] channelNames;

    private Map<String, NotificationChannel> channelModelMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (EditText) findViewById(R.id.messageView);
        Button postButton = (Button) findViewById(R.id.postButton);
        Spinner channelSpinner = (Spinner) findViewById(R.id.channelSpinner);

        channelModelMap = new HashMap<>();
        channelModelMap.put(Constants.PUBLIC_CHANNEL, createPublicGroupMessageChannel());
        channelModelMap.put(Constants.PRIVATE_CHANNEL, createPrivateGroupMessageChannel());
        channelModelMap.put(Constants.DIRECT_CHANNEL, createDirectMessageChannel());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannels(getChannels());

        channelNames = getChannelNames();
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, channelNames);
        channelSpinner.setAdapter(adapter);

        channelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentNotificationChannelName = channelNames[i];
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                notificationCount++;
                Notification notification = buildNotification(MainActivity.this, currentNotificationChannelName, notificationCount,
                        messageView.getText().toString());
                notificationManager.notify(notificationCount, notification);
            }
        });
    }

    private List<NotificationChannel> getChannels() {
        return new ArrayList<>(channelModelMap.values());
    }

    private String getChannelIdFromName(String name) {
        return channelModelMap.get(name).getId();
    }

    private String[] getChannelNames() {
        return channelModelMap.keySet().toArray(new String[channelModelMap.size()]);
    }

    private NotificationChannel createPublicGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel(Constants.PUBLIC_CHANNEL, NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(true);
        return channel;
    }

    private NotificationChannel createPrivateGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel(Constants.PRIVATE_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.BLUE);
        return channel;
    }

    private NotificationChannel createDirectMessageChannel() {
        NotificationChannel channel = createNotificationChannel(Constants.DIRECT_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.RED);
        channel.setBypassDnd(true);
        return channel;
    }

    private NotificationChannel createNotificationChannel(String name, int importance) {
        return new NotificationChannel(name.toLowerCase() + "-channel", name, importance);
    }

    private Notification buildNotification(Context context, String currentNotificationChannelName, int id, String text) {
        Icon icon = Icon.createWithResource(context, R.mipmap.ic_launcher_round);
        Notification.Builder builder = new Notification.Builder(context, getChannelIdFromName(currentNotificationChannelName))
                .setContentTitle(currentNotificationChannelName)
                .setSmallIcon(icon)
                .setContentText(text);

        switch (currentNotificationChannelName) {
            case Constants.PUBLIC_CHANNEL:
            case Constants.PRIVATE_CHANNEL:
                builder.addAction(createAction(context, Constants.SNOOZE_REQ_CODE, id, R.drawable.ic_snooze_black_24dp,
                        Constants.SNZOOE_ACTION));
                break;
            case Constants.DIRECT_CHANNEL:
                builder.addAction(createAction(context, Constants.REPLY_REQ_CODE, id, R.drawable.ic_reply_black_24dp,
                        Constants.REPLY_ACTION));
                builder.addAction(createAction(context, Constants.BLOCK_REQ_CODE, id, R.drawable.ic_block_black_24dp, Constants.BLOCK_ACTON));
                break;
            default:
                throw new IllegalStateException("Not Support Channel name.");
        }

        return builder.build();
    }

    private Notification.Action createAction(Context context, int requestCode, int notificaitonId, @DrawableRes int iconId, String action) {
        Intent intent = new Intent(context, NotificationBoardcastReceiver.class);
        intent.setAction(action);
        intent.putExtra(Constants.NOTIFICATION_ID, notificaitonId);
        intent.putExtra(Constants.REQUEST_CODE, requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Icon icon = Icon.createWithResource(context, iconId);
        return new Notification.Action.Builder(icon, action, pendingIntent)
                .build();
    }
}
