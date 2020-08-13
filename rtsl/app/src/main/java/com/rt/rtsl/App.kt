package com.rt.rtsl

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.rt.rtsl.utils.AssetsManager
import com.rt.rtsl.utils.ExceptionHandler
import com.rt.rtsl.utils.FileUtil
import com.rtsl.app.android.R
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import org.jetbrains.anko.doAsync


class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        doAsync {
            ExceptionHandler.getInstance().init(this@App)
            val map = HashMap<String, Any>()
            map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
            map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
            try {
                QbSdk.initTbsSettings(map)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
            val cb: PreInitCallback = object : PreInitCallback {
                override fun onViewInitFinished(arg0: Boolean) {
                    // TODO Auto-generated method stub
                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                    Log.d("app", " onViewInitFinished is $arg0")
                }

                override fun onCoreInitFinished() {
                    Log.d("app", " onCoreInitFinished")
                }
            }
            try {
                QbSdk.initX5Environment(this@App,cb)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
    }
    companion object
    {
        @JvmStatic
        fun initFile(context: Context)
        {
            doAsync {
                FileUtil.initPackage(context)
                var use_terms=context.resources.getString(R.string.use_terms)
                AssetsManager.copyAssetFile2SDCard(context,use_terms,FileUtil.createFile("${FileUtil.CACHE}${use_terms}"))
                var app_policy=context.resources.getString(R.string.app_policy)
                AssetsManager.copyAssetFile2SDCard(context,app_policy,FileUtil.createFile("${FileUtil.CACHE}${app_policy}"))
            }
        }
    }
}