package com.androidants.sampleapp.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.androidants.sampleapp.ui.splash.SplashActivity


class StartOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val activityIntent = Intent(context, SplashActivity::class.java)
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.d(Constants.TAG , "starting app")
            context.startActivity(activityIntent)
        }
    }
}