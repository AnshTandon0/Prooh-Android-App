package com.androidants.sampleapp.common

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.androidants.sampleapp.VinciisCreation
import com.androidants.sampleapp.ui.splash.SplashActivity


class MyExceptionHandler(private var activity: Activity?) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        val intent = Intent(activity, SplashActivity::class.java)
        intent.putExtra("crash", true)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )

        val pendingIntent = PendingIntent.getActivity(
            VinciisCreation.instance?.getBaseContext(),
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val agr : AlarmManager = VinciisCreation.instance?.getBaseContext()?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        agr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)

        activity!!.finish()
        System.exit(2)
    }
}
