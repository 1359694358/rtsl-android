package com.rt.rtsl

import androidx.multidex.MultiDexApplication
import com.rt.rtsl.utils.ExceptionHandler

class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        ExceptionHandler.getInstance().init(this)
    }
}