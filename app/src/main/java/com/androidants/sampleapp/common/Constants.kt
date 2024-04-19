package com.androidants.sampleapp.common

import com.androidants.sampleapp.R
import kotlinx.coroutines.CoroutineExceptionHandler

object Constants {

    // base url
    val BASE_URL = "https://beta.vinciis.in/"
    val DEFAULT_WEBVIEW_URL = ""


    // screen code generation
    val RANDOM_STRING_LENGTH = 6


    // folder paths
    val INITIAL_VIDEO_PATH = "android.resource://com.androidants.sampleapp/" + R.raw.initial
    val DOWNLOAD_FOLDER_PATH = "/storage/emulated/0/Download/"


    // tag for log
    val TAG_NORMAL = "MAIN ACTIVITY VINCIIS"
    val TAG_ERROR = "MAIN ACTIVITY ERROR"


    // file type
    val TYPE_VIDEO = "video"
    val TYPE_IMAGE = "image"
    val TYPE_URL = "url"
    val TYPE_YOUTUBE = "youtube"
    val DOT = "."

    // list type
    val ACTIVE_CAMPAIGN_LIST = "active_list"
    val PAUSED_CAMPAIGN_LIST = "paused_list"
    val HOLD_CAMPAIGN_LIST = "hold_list"


    // shared preferences
    val SHARED_PREF_NAME = "vinciis"
    val SHARED_PREF_SCREEN_CODE = "screen_code"
    val SHARED_PREF_SCREEN_ID = "screen_id"
    val SHARED_PREF_SUCCESS_ID_SET = "success_download"
    val SHARED_PREF_FAILURE_ID_SET = "failure_download"
    val SHARED_PREF_DOWNLOADING_ID_SET = "downloading_download"
    val SHARED_PREF_FILE_DATA = "file_data"
    val SHARED_PREF_LOG_REPORT = "log_report"
    val SHARED_PREF_RESTART_STATUS = "restart_status"


    // coroutine exception
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
}