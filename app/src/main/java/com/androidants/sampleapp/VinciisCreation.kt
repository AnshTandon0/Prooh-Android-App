package com.androidants.sampleapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VinciisCreation : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }

    companion object {
        var instance: VinciisCreation? = null
    }
}