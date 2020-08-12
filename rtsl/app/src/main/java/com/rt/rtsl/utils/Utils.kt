package com.rt.rtsl.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log

fun Context.startActivity(activity: String) {
    val intent = Intent()
    intent.setClassName(this, activity)
    startActivity(intent)
}

fun Context.startActivity(clasz: Class<out Activity>) {
    val intent = Intent()
    intent.setClass(this, clasz)
    startActivity(intent)
}

inline fun  Any.logd(message: () -> String) = Log.d(javaClass.simpleName, message())

inline fun  Any.logd(message:String) = Log.d(javaClass.simpleName, message)

inline fun Any.logd(message:Any) = Log.d(javaClass.simpleName, message.toString())

inline fun  Any.loge(error: Throwable, message: () -> String) = Log.d(javaClass.simpleName, message(), error)

inline fun  Any.loge(error: Throwable, message:String?="") = Log.d(javaClass.simpleName, message, error)


inline fun <reified T> getParcelableArrayExtra(parcels:  Array<Parcelable> ): Array<T?>
{
    var array=arrayOfNulls<T>(parcels.size)
    parcels.forEachIndexed { index, parcelable ->
        array[index]=parcelable as T
    }
    return array
}
