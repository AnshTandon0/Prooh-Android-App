package com.androidants.sampleapp.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.LogReport
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SharedPreferencesClass ( context: Context ) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.SHARED_PREF_NAME , Context.MODE_PRIVATE)
    }
    private val editor : SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }

    fun addDownloadingId ( id : String )
    {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_DOWNLOADING_ID_SET , mutableSetOf())
        videoSet?.add(id)
        editor.putStringSet(Constants.SHARED_PREF_DOWNLOADING_ID_SET , videoSet)
        editor.commit()
    }

    fun deleteDownloadingId ( id : String )
    {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_DOWNLOADING_ID_SET , mutableSetOf())
        videoSet?.remove(id)
        editor.putStringSet(Constants.SHARED_PREF_DOWNLOADING_ID_SET , videoSet)
        editor.commit()
    }

    fun checkDownloadingIdExists ( id : String ) : Boolean
    {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_DOWNLOADING_ID_SET , mutableSetOf())
        if ( videoSet?.contains(id) == true )
            return true
        return false
    }

    fun addSuccessId( id : String ) {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , mutableSetOf())
        videoSet?.add(id)
        editor.putStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , videoSet)
        editor.commit()
    }

    fun addFailureId( id : String ) {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_FAILURE_ID_SET , mutableSetOf())
        videoSet?.add(id)
        editor.putStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , videoSet)
        editor.commit()
    }

    fun deleteSuccessId ( id : String ) {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , mutableSetOf())
        videoSet?.remove(id)
        editor.putStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , videoSet)
        editor.commit()
    }

    fun deleteAllDownloadingId () {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_DOWNLOADING_ID_SET , mutableSetOf())
        videoSet?.clear()
        editor.putStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , videoSet)
        editor.commit()
    }

    fun deleteAllFailureId () {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_FAILURE_ID_SET , mutableSetOf())
        videoSet?.clear()
        editor.putStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , videoSet)
        editor.commit()
    }

    fun getAllSuccessId () : MutableSet<String> {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_FAILURE_ID_SET , mutableSetOf())
        return videoSet ?: mutableSetOf()
    }

    fun deleteFailureId ( id : String ) {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_FAILURE_ID_SET, mutableSetOf())
        videoSet?.remove(id)
        editor.putStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , videoSet)
        editor.commit()
    }

    fun checkSuccessIdExists ( id: String ) : Boolean {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , mutableSetOf())
        if ( videoSet?.contains(id) == true )
            return true
        return false
    }

    fun checkSuccessEmpty() : Boolean {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_SUCCESS_ID_SET , mutableSetOf())
        if ( videoSet?.size == 0 )
            return true
        return false
    }

    fun checkFailureIdExists ( id: String ) : Boolean {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_FAILURE_ID_SET , mutableSetOf())
        if ( videoSet?.contains(id) == true )
            return true
        return false
    }

    fun getScreenCode () : String {
        val screenCode = sharedPreferences.getString(Constants.SHARED_PREF_SCREEN_CODE , "")
        return screenCode.toString()
    }

    fun setScreenCode (code : String) {
        editor.putString(Constants.SHARED_PREF_SCREEN_CODE , code)
        editor.commit()
    }

    fun getScreenId () : String {
        val screenId = sharedPreferences.getString(Constants.SHARED_PREF_SCREEN_ID , "")
        return screenId.toString()
    }

    fun setScreenId (id : String) {
        editor.putString(Constants.SHARED_PREF_SCREEN_ID , id)
        editor.commit()
    }

    fun saveFileData (list : MutableList<VideoData>) {
        val gson = Gson()
        val data = gson.toJson(list)
        editor.putString(Constants.SHARED_PREF_FILE_DATA , data)
        editor.commit()
    }

    fun getFileData () : ArrayList<VideoData> {
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<VideoData>>() {}.type
        val data = sharedPreferences.getString(Constants.SHARED_PREF_FILE_DATA , "")
        val list = gson.fromJson<Any>(data , type) as ArrayList<VideoData>
        return list
    }

    fun saveLogs (logReport: LogReport) {
        val gson = Gson()
        val data = gson.toJson(logReport)
        editor.putString(Constants.SHARED_PREF_LOG_REPORT , data)
        editor.commit()
    }

    fun checkLogs () : Boolean{
        return sharedPreferences.contains(Constants.SHARED_PREF_LOG_REPORT)
    }

    fun getLogs () : LogReport {
        val gson = Gson()
        val type: Type = object : TypeToken<LogReport>() {}.type
        val data = sharedPreferences.getString(Constants.SHARED_PREF_LOG_REPORT , "")
        val logs = gson.fromJson<Any>(data , type) as LogReport
        return logs
    }

    fun saveRestartStatus(restart : Boolean) {
        editor.putBoolean(Constants.SHARED_PREF_RESTART_STATUS , restart)
        editor.commit()
    }

    fun getRestartStatus() : Boolean {
        return sharedPreferences.getBoolean(Constants.SHARED_PREF_RESTART_STATUS , false)
    }
}