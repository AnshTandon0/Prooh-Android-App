package com.androidants.sampleapp.common

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DownloadCompleteReceiver : BroadcastReceiver() {

    private lateinit var downloadManager  : DownloadManager
    private lateinit var sharedPreferencesClass: SharedPreferencesClass

    override fun onReceive(context: Context?, intent: Intent?) {
        downloadManager = context?.getSystemService(DownloadManager::class.java)!!
        sharedPreferencesClass = SharedPreferencesClass(context)

        if ( intent?.action == "android.intent.action.DOWNLOAD_COMPLETE" )
        {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID , -1L)
            if ( id != -1L )
            {
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst() && cursor.count > 0 ) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            sharedPreferencesClass.addSuccessId(id.toString())
                        }
                        DownloadManager.STATUS_FAILED -> {
                            sharedPreferencesClass.addFailureId(id.toString())
                        }
                    }
                }
            }

        }
    }
}