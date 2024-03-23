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
}