package com.rt.rtsl

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDexApplication
import com.mediacloud.app.share.SocialShareControl
import com.pgyersdk.crash.PgyCrashManager
import com.rt.rtsl.utils.AssetsManager
import com.rt.rtsl.utils.ExceptionHandler
import com.rt.rtsl.utils.FileUtil
import com.rtsl.app.android.R
import com.tencent.mmkv.MMKV
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.youzan.androidsdk.YouzanSDK
import com.youzan.androidsdkx5.YouZanSDKX5Adapter
import com.youzan.androidsdkx5.YouzanPreloader
import org.jetbrains.anko.doAsync


class App: MultiDexApplication(), ViewModelStoreOwner {
    private val mAppViewModelStore=ViewModelStore()

    private var mFactory: ViewModelProvider.Factory? = null

    override fun onCreate() {
        super.onCreate()
        YouzanSDK.init(this@App, resources.getString(R.string.youzan_clientId),YouZanSDKX5Adapter())
        YouzanPreloader.preloadHtml(this@App, resources.getString(R.string.youzan_storeurl));
        doAsync {
            ExceptionHandler.getInstance().init(this@App)
            ExceptionHandler.getInstance().setCrashCallBack {
                PgyCrashManager.reportCaughtException(it as java.lang.Exception?)
            }
            //
            var use_terms=this@App.resources.getString(R.string.use_terms)
            var cacheDir=this@App.cacheDir.absolutePath
            Log.d("AppCache","cacheDir:$cacheDir")
            AssetsManager.copyAssetFile2SDCard(this@App,use_terms,FileUtil.createFile("${cacheDir}${use_terms}"))
            var app_policy=this@App.resources.getString(R.string.app_policy)
            AssetsManager.copyAssetFile2SDCard(this@App,app_policy,FileUtil.createFile("${cacheDir}${app_policy}"))
            MMKV.initialize(this@App)

            SocialShareControl.initAppKey(this@App)
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

             val cb: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
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
            }
        }
    }

    fun getAppViewModelProvider(activity: Activity): ViewModelProvider
    {
        return ViewModelProvider(this,(activity.applicationContext as App).getAppFactory(activity))
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return mFactory!!
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
                ?: throw IllegalStateException("Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call.")
    }

    private fun checkActivity(fragment: Fragment): Activity? {
        return fragment.activity
                ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }
}