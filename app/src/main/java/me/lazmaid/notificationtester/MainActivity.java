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
import android.text.Editable;
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

        messageView = (EditText) findViewById(R.id.messageView);
        Button postButton = (Button) findViewById(R.id.postButton);
        Spinner channelSpinner = (Spinner) findViewById(R.id.channelSpinner);

        channelModelMap = new HashMap<>();
        channelModelMap.put(Channel.PUBLIC, createPublicGroupMessageChannel());
        channelModelMap.put(Channel.PRIVATE, createPrivateGroupMessageChannel());
        channelModelMap.put(Channel.DIRECT, createDirectMessageChannel());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannels(getChannels());

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
                Editable text = messageView.getText();
                if (text != null && !text.toString().isEmpty()) {
                    notificationCount++;
                    Notification notification = buildNotification(MainActivity.this, currentChannel, notificationCount,
                            messageView.getText().toString());
                    notificationManager.notify(notificationCount, notification);
                } else {
                    Toast.makeText(MainActivity.this, "Notification Message is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String [] getChannelNames() {
        String[] channelNames = new String[Channel.values().length];
        for (int i = 0; i < Channel.values().length; i++) {
            channelNames[i] = Channel.values()[i].toCapitalizeName();
        }
        return channelNames;
    }

    private List<NotificationChannel> getChannels() {
        return new ArrayList<>(channelModelMap.values());
    }

    private String getChannelIdFromName(Channel channel) {
        return channelModelMap.get(channel).getId();
    }

    private NotificationChannel createPublicGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel(Channel.PUBLIC.toCapitalizeName(), NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(true);
        return channel;
    }

    private NotificationChannel createPrivateGroupMessageChannel() {
        NotificationChannel channel = createNotificationChannel(Channel.PRIVATE.toCapitalizeName(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.BLUE);
        return channel;
    }

    private NotificationChannel createDirectMessageChannel() {
        NotificationChannel channel = createNotificationChannel(Channel.DIRECT.toCapitalizeName(), NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setShowBadge(true);
        channel.setLightColor(Color.RED);
        channel.setBypassDnd(true);
        return channel;
    }

    private NotificationChannel createNotificationChannel(String name, int importance) {
        return new NotificationChannel(name.toLowerCase() + "-channel", name, importance);
    }

    private Notification buildNotification(Context context, Channel currentChannel, int id, String text) {
        Icon icon = Icon.createWithResource(context, R.mipmap.ic_launcher_round);
        Notification.Builder builder = new Notification.Builder(context, getChannelIdFromName(currentChannel))
                .setContentTitle(currentChannel.toCapitalizeName())
                .setSmallIcon(icon)
                .setContentText(text);

        switch (currentChannel) {
            case PUBLIC:
            case PRIVATE:
                builder.addAction(createAction(context, Constants.SNOOZE_REQ_CODE, id, R.drawable.ic_snooze_black_24dp,
                        Constants.SNZOOE_ACTION));
                break;
            case DIRECT:
                builder.addAction(createAction(context, Constants.REPLY_REQ_CODE, id, R.drawable.ic_reply_black_24dp,
                        Constants.REPLY_ACTION));
                builder.addAction(createAction(context, Constants.BLOCK_REQ_CODE, id, R.drawable.ic_block_black_24dp, Constants.BLOCK_ACTON));
                break;
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
