package com.rt.rtsl

import androidx.multidex.MultiDexApplication
import com.rt.rtsl.utils.ExceptionHandler
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import org.jetbrains.anko.doAsync


class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        doAsync {
            ExceptionHandler.getInstance().init(this@App)
            val map = HashMap<String, Any>()
            map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
            map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
            QbSdk.initTbsSettings(map)
        }
    }
}