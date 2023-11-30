package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class NotificationWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent
        if (shouldNotify){
            val detail = Intent(ctx, DetailHabitActivity::class.java)
            detail.putExtra(HABIT_ID, habitId)

            val pendingIntent = PendingIntent.getActivity(ctx, 0 , detail, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val message = ctx.getString(R.string.notify_content)
            val ringtones = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val title = habitTitle


            val nManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notifBuilder = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(longArrayOf(1000,1000,1000,1000))
                .setSound(ringtones)
                .setColor(ContextCompat.getColor(ctx, android.R.color.transparent))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "notify-habit", NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000,1000,1000,1000)
                notifBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                nManager.createNotificationChannel(channel)
            }
            notifBuilder.setAutoCancel(true)
            val notif = notifBuilder.build()
            nManager.notify(1, notif)
        }

        return Result.success()
    }

}
