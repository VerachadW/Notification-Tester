package me.lazmaid.notificationtester

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityKt : AppCompatActivity() {

    private val channelModelMap = mapOf(
            ChannelKt.PUBLIC to createNotificationChannel(ChannelKt.PUBLIC),
            ChannelKt.PRIVATE to createNotificationChannel(ChannelKt.PRIVATE),
            ChannelKt.DIRECT to createNotificationChannel(ChannelKt.DIRECT)
    )

    //Lazy Initialization
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var notificationCount = 0

    // Will be init later
    private lateinit var currentChannel: ChannelKt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up
        currentChannel = ChannelKt.values()[0]
        notificationManager.createNotificationChannels(channelModelMap.values.toList())

        // View setup
        channelSpinner.apply {
            adapter = ArrayAdapter<String>(this@MainActivityKt, android.R.layout.simple_spinner_item,
                    ChannelKt.values().map { it.displayName })
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}

                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currentChannel = ChannelKt.values()[position]
                }
            }
        }

        postButton.setOnClickListener {
            val text = messageView.text.toString()
            if (text.isNullOrBlank()) {
                Toast.makeText(this@MainActivityKt, "Notification Message is Empty", Toast.LENGTH_SHORT).show()
            } else {
                notificationCount++
                val notification = buildNotification(currentChannel, notificationCount, text)
                notificationManager.notify(notificationCount, notification)
            }
        }
    }

    private fun createNotificationChannel(channel: ChannelKt): NotificationChannel {
        return when (channel) {
            ChannelKt.PUBLIC -> {
                NotificationChannel(channel.id, channel.displayName, NotificationManager.IMPORTANCE_LOW).apply {
                    setShowBadge(true)
                }
            }
            ChannelKt.PRIVATE -> {
                NotificationChannel(channel.id, channel.displayName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    enableLights(true)
                    setShowBadge(true)
                    lightColor = Color.BLUE
                }
            }
            ChannelKt.DIRECT -> {
                NotificationChannel(channel.id, channel.displayName, NotificationManager.IMPORTANCE_HIGH).apply {
                    setShowBadge(true)
                    setBypassDnd(true)
                    enableLights(true)
                    lightColor = Color.RED
                }
            }
        }
    }

    private fun buildNotification(channel: ChannelKt, id: Int, message: String) =
            with(Notification.Builder(this, channel.id)) {
                setContentTitle(channel.displayName)
                setSmallIcon(R.mipmap.ic_launcher_round)
                setContentText(message)

                when (channel) {
                    ChannelKt.PUBLIC, ChannelKt.PRIVATE -> {
                        addAction(buildAction(SNOOZE_REQ_CODE, id, R.drawable.ic_snooze_black_24dp,
                                SNZOOE_ACTION))
                    }
                    ChannelKt.DIRECT -> {
                        addAction(buildAction(REPLY_REQ_CODE, id, R.drawable.ic_reply_black_24dp,
                                REPLY_ACTION))
                        addAction(buildAction(BLOCK_REQ_CODE, id, R.drawable.ic_block_black_24dp,
                                BLOCK_ACTON))
                    }
                }
                build()
            }

    private fun buildAction(requestCode: Int, notificationId: Int, @DrawableRes iconId: Int, actionName: String): Notification.Action {
        val intent = Intent(this, NotificationBoardcastReceiverKt::class.java).apply {
            action = actionName
            putExtra(NOTIFICATION_ID, notificationId)
            putExtra(REQUEST_CODE, requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val icon = Icon.createWithResource(this, iconId)
        return Notification.Action.Builder(icon, actionName, pendingIntent).build()
    }
}