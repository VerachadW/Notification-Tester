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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText messageView;

    private NotificationManager notificationManager;
    private Channel currentChannel;
    private int notificationCount;

    private Map<Channel, NotificationChannel> channelModelMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization
        currentChannel = Channel.values()[0];

        channelModelMap = new HashMap<>();
        channelModelMap.put(Channel.PUBLIC, createNotificationChannel(Channel.PUBLIC));
        channelModelMap.put(Channel.PRIVATE, createNotificationChannel(Channel.PRIVATE));
        channelModelMap.put(Channel.DIRECT, createNotificationChannel(Channel.DIRECT));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannels(getChannels());

        // View Initialization
        messageView = (EditText) findViewById(R.id.messageView);
        Button postButton = (Button) findViewById(R.id.postButton);
        Spinner channelSpinner = (Spinner) findViewById(R.id.channelSpinner);

        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getChannelNames());
        channelSpinner.setAdapter(adapter);

        channelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentChannel = Channel.values()[i];
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String text = messageView.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Notification Message is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    notificationCount++;
                    Notification notification = buildNotification(currentChannel, notificationCount, text);
                    notificationManager.notify(notificationCount, notification);
                }
            }
        });
    }

    private NotificationChannel createNotificationChannel(Channel channel) {
        switch (channel) {
            case PUBLIC:
                return createPublicGroupMessageChannel();
            case PRIVATE:
                return createPrivateGroupMessageChannel();
            case DIRECT:
                return createDirectMessageChannel();
            default:
                throw new IllegalStateException("Not supported Group");
        }
    }

    private Notification buildNotification(Channel channel, int id, String message) {
        Notification.Builder builder = new Notification.Builder(this, channel.getChannelId());

        builder.setContentTitle(channel.getDisplayName())
               .setSmallIcon(R.mipmap.ic_launcher_round)
               .setContentText(message);

        switch (channel) {
            case PUBLIC:
            case PRIVATE:
                builder.addAction(buildAction(Constants.SNOOZE_REQ_CODE, id, R.drawable.ic_snooze_black_24dp,
                        Constants.SNZOOE_ACTION));
                break;
            case DIRECT:
                builder.addAction(buildAction(Constants.REPLY_REQ_CODE, id, R.drawable.ic_reply_black_24dp,
                        Constants.REPLY_ACTION));
                builder.addAction(buildAction(Constants.BLOCK_REQ_CODE, id, R.drawable.ic_block_black_24dp,
                        Constants.BLOCK_ACTON));
                break;
        }

        return builder.build();
    }

    private Notification.Action buildAction(int requestCode, int notificaitonId, @DrawableRes int iconId, String actionName) {
        Intent intent = new Intent(this, NotificationBoardcastReceiver.class);
        intent.setAction(actionName);
        intent.putExtra(Constants.NOTIFICATION_ID, notificaitonId);
        intent.putExtra(Constants.REQUEST_CODE, requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Icon icon = Icon.createWithResource(this, iconId);
        return new Notification.Action.Builder(icon, actionName, pendingIntent)
                .build();
    }

    private String[] getChannelNames() {
        String[] channelNames = new String[Channel.values().length];
        for (int i = 0; i < Channel.values().length; i++) {
            channelNames[i] = Channel.values()[i].getDisplayName();
        }
        return channelNames;
    }

    private List<NotificationChannel> getChannels() {
        return new ArrayList<>(channelModelMap.values());
    }

    private NotificationChannel createPublicGroupMessageChannel() {
        String id = Channel.PUBLIC.getChannelId();
        String displayName = Channel.PUBLIC.getDisplayName();
        NotificationChannel channel = new NotificationChannel(id, displayName, NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(true);
        return channel;
    }

    private NotificationChannel createPrivateGroupMessageChannel() {
        String id = Channel.PRIVATE.getChannelId();
        String displayName = Channel.PRIVATE.getDisplayName();
        NotificationChannel channel = new NotificationChannel(id, displayName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.BLUE);
        return channel;
    }

    private NotificationChannel createDirectMessageChannel() {
        String id = currentChannel.getChannelId();
        String displayName = Channel.PRIVATE.getDisplayName();
        NotificationChannel channel = new NotificationChannel(id, displayName, NotificationManager.IMPORTANCE_HIGH);
        channel.setBypassDnd(true);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        return channel;
    }
}