package com.androidants.sampleapp.common

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesClass ( context: Context ) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(Constants.SHARED_PREF_NAME , Context.MODE_PRIVATE)
    }
    private val editor : SharedPreferences.Editor by lazy {
        sharedPreferences.edit()
    }

    fun getVideoIdSet() : Set<String> {
        return sharedPreferences.getStringSet(Constants.SHARED_PREF_VIDEO_ID_SET , mutableSetOf() ) as Set<String>
    }

    fun addVideoId( id : String ) {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_VIDEO_ID_SET , mutableSetOf())
        videoSet?.add(id)
        editor.putStringSet(Constants.SHARED_PREF_VIDEO_ID_SET , videoSet)
        editor.commit()
    }

    fun deleteVideoId ( id : String ) {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_VIDEO_ID_SET , mutableSetOf())
        videoSet?.remove(id)
        editor.putStringSet(Constants.SHARED_PREF_VIDEO_ID_SET , videoSet)
        editor.commit()
    }

    fun checkIdExists ( id: String ) : Boolean {
        val videoSet : MutableSet<String>? = sharedPreferences.getStringSet(Constants.SHARED_PREF_VIDEO_ID_SET , mutableSetOf())
        if ( videoSet?.contains(id) == true )
            return true
        return false
    }
}