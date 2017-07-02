package me.lazmaid.notificationtester;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivity extends AppCompatActivity {

    EditText messageView;
    Button postButton;
    Spinner channelSpinner;

    NotificationManager notificationManager;
    String currentNotificationChannelName;
    int notificationCount;

    String[] channelNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (EditText) findViewById(R.id.messageView);
        postButton = (Button) findViewById(R.id.postButton);
        channelSpinner = (Spinner) findViewById(R.id.channelSpinner);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationChannelProvider provider = NotificationChannelProvider.getInstnace();

        notificationManager.createNotificationChannels(provider.getChannels());

        channelNames = provider.getChannelNames();
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
                Notification notification = new Notification.Builder(MainActivity.this, provider.getChannelIdFromName(currentNotificationChannelName))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(currentNotificationChannelName)
                        .setContentText(messageView.getText())
                        .setNumber(notificationCount)
                        .build();
                notificationManager.notify(notificationCount, notification);
            }
        });
    }
}
