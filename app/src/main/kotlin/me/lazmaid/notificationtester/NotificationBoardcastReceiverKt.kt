package me.lazmaid.notificationtester

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationBoardcastReceiverKt : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val requestCode = it.getIntExtra(REQUEST_CODE, 0)
            val notificationId = it.getIntExtra(NOTIFICATION_ID, 0)

            val action = when (requestCode) {
                SNOOZE_REQ_CODE -> SNZOOE_ACTION
                BLOCK_REQ_CODE -> BLOCK_ACTON
                REPLY_REQ_CODE -> REPLY_ACTION
                else -> ""
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)

            Toast.makeText(context, action, Toast.LENGTH_SHORT).show()
        }
    }
}