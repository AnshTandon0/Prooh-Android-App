package com.androidants.sampleapp.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androidants.sampleapp.VinciisCreation
import com.androidants.sampleapp.ui.splash.SplashActivity


class StartOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
//             method 1
            val activityIntent = Intent(context, SplashActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(
                VinciisCreation.instance?.getBaseContext(),
                0,
                activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val agr : AlarmManager = VinciisCreation.instance?.getBaseContext()?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            agr.set(AlarmManager.RTC, System.currentTimeMillis() + 60000, pendingIntent)


            // method 2
//            val packageManager : PackageManager = context.getPackageManager()
//            val intent : Intent = packageManager.getLaunchIntentForPackage(context.getPackageName())!!
//            val componentName : ComponentName = intent.getComponent()!!
//            val mainIntent = Intent.makeRestartActivityTask(componentName);
//            // Required for API 34 and later
//            // Ref: https://developer.android.com/about/versions/14/behavior-changes-14#safer-intents
//            mainIntent.setPackage(context.getPackageName())
//            context.startActivity(mainIntent)


            // method 3
//            val i = Intent(context, SplashActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context.startActivity(i)
        }
    }
}