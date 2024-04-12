package com.androidants.sampleapp.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.androidants.sampleapp.VinciisCreation
import com.androidants.sampleapp.ui.splash.SplashActivity


class StartOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val activityIntent = Intent(context, SplashActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(
                VinciisCreation.instance?.getBaseContext(),
                0,
                activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val agr : AlarmManager = VinciisCreation.instance?.getBaseContext()?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            agr.set(AlarmManager.RTC, System.currentTimeMillis() + 60000, pendingIntent)
        }
    }
}