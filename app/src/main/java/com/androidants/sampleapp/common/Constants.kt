package com.androidants.sampleapp.common

import com.androidants.sampleapp.R
import kotlinx.coroutines.CoroutineExceptionHandler

object Constants {

    // base url
    val BASE_URL = "https://api.justmonad.com/"


    // screen code generation
    val RANDOM_STRING_LENGTH = 6


    // status
    val STATUS_DOWNLOADING = "downloading"
    val STATUS_DONE = "done"


    // folder paths
    val INITIAL_VIDEO_PATH = "android.resource://com.androidants.sampleapp/" + R.raw.sample
    val DOWNLOAD_FOLDER_PATH = "/storage/emulated/0/Download/"


    // tag for log
    val TAG = "MAIN ACTIVITY VINCIIS"


    // file type
    val TYPE_VIDEO = "video/mp4"
    val TYPE_IMAGE = "image/jpeg"
    val TYPE_URL = "url"
    val VIDEO_TYPE = ".mp4"
    val IMAGE_TYPE = ".jpeg"


    // shared preferences
    val SHARED_PREF_NAME = "vinciis"
    val SHARED_PREF_SCREEN_CODE = "screen_code"
    val SHARED_PREF_SCREEN_ID = "screen_id"
    val SHARED_PREF_SUCCESS_ID_SET = "success_download"
    val SHARED_PREF_FAILURE_ID_SET = "failure_download"
    val SHARED_PREF_FILE_DATA = "file_data"

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
}