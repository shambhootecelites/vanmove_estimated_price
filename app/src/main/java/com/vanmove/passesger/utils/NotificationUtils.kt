package com.vanmove.passesger.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.vanmove.passesger.BuildConfig
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.universal.AppController
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtils {
    private val notificationManager: NotificationManager =
        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val orderChannelId = BuildConfig.APPLICATION_ID

    fun showNotification(
        title: String,
        message: String,
        notification_name: String = "",
        postParam: HashMap<String, String> = HashMap()
    ) {
        val notificationId = createNotificationId()
        val builder = NotificationCompat.Builder(mContext, orderChannelId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // you must create a notification channel for API 26 and Above
            createChannel()
            builder.setChannelId(orderChannelId)
        }
        builder.setSmallIcon(notificationIcon)
            .setWhen(0)
            .setStyle(getNotificationStyle(message))
            .setContentTitle(title)
            .setContentText(message)
            .setColor(Color.parseColor("#ff512da8"))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(false)

        if (notification_sound != null) {
            builder.setSound(notification_sound)
        }
        val intent = Intent(mContext, MainScreenActivity::class.java)
            .apply {
                putExtra(
                    CONSTANTS.INTENT_EXTRA_NOTIFICATION_NAME, notification_name
                )
                putExtra(
                    CONSTANTS.INTENT_EXTRA_IS_PUSH_NOTIFICATION_AVAILABLE,
                    true
                )
                postParam.forEach {
                    putExtra(
                        it.key,
                        it.value
                    )
                }
            }
        val pendingFlags: Int
        pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(
            mContext,
            0, intent, pendingFlags
        )
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        notificationManager.notify(notificationId, notification)

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel() {
        NotificationChannel(
            orderChannelId,
            "Salaat Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            description = "Channel to show Salaat notifications."
        }.also {
            notificationManager.createNotificationChannel(it)
        }

    }

    private fun getNotificationStyle(line: String): NotificationCompat.InboxStyle {
        val inboxStyle =
            NotificationCompat.InboxStyle()
        inboxStyle.addLine(line)
        return inboxStyle
    }


    companion object {
        val notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        private val mContext: Context = AppController.getAppContext()
        fun createNotificationId(): Int {
            val now = Date()
            return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
        }
    }

    val notificationIcon: Int
        get() {
            val useWhiteIcon =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (useWhiteIcon) R.drawable.ic_logo_transparent else R.mipmap.logo
        }

}