package com.rt.rtsl.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

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