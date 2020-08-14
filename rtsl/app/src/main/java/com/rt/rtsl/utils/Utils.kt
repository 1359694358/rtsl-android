package com.rt.rtsl.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import com.tencent.mmkv.MMKV
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

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

fun WebView.setWebSetting() {
    if (Build.VERSION.SDK_INT >= 17) {
        getSettings().setMediaPlaybackRequiresUserGesture(false)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getSettings().setSafeBrowsingEnabled(false)
    }
    if (Build.VERSION.SDK_INT >= 19)
        setWebContentsDebuggingEnabled(true)
    getSettings().setDatabaseEnabled(true)
    setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
    val dir: String =getContext().getDir("database", Context.MODE_PRIVATE).getPath()
    getSettings().setGeolocationDatabasePath(dir)
    getSettings().setGeolocationEnabled(true)
    getSettings().setDomStorageEnabled(true)
    getSettings().setJavaScriptEnabled(true)
    getSettings().setAllowFileAccess(true) // 允许访问文件
    getSettings().setDefaultTextEncodingName("UTF-8")
    //        getSettings().setAppCacheEnabled(false);
    getSettings().setSavePassword(false)
    getSettings().setCacheMode(WebSettings.LOAD_DEFAULT)
    if (Build.VERSION.SDK_INT >= 21) {
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
}

fun com.tencent.smtt.sdk.WebView.setWebSetting() {
    if (Build.VERSION.SDK_INT >= 17) {
        getSettings().setMediaPlaybackRequiresUserGesture(false)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        getSettings().setSafeBrowsingEnabled(false)
    }
    if (Build.VERSION.SDK_INT >= 19)
        com.tencent.smtt.sdk.WebView.setWebContentsDebuggingEnabled(true)
    getSettings().setDatabaseEnabled(true)
    setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
    val dir: String =getContext().getDir("database", Context.MODE_PRIVATE).getPath()
    getSettings().setGeolocationDatabasePath(dir)
    getSettings().setGeolocationEnabled(true)
    getSettings().setDomStorageEnabled(true)
    getSettings().setJavaScriptEnabled(true)
    getSettings().setAllowFileAccess(true) // 允许访问文件
    getSettings().setDefaultTextEncodingName("UTF-8")
    //        getSettings().setAppCacheEnabled(false);
    getSettings().setSavePassword(false)
    getSettings().setCacheMode(WebSettings.LOAD_DEFAULT)
    if (Build.VERSION.SDK_INT >= 21) {
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
}

fun saveAny(key:String, obj:Any)
{
    var bos= ByteArrayOutputStream()
    var oos= ObjectOutputStream(bos)
    oos.writeObject(obj)
    var saved= MMKV.defaultMMKV().encode(key,bos.toByteArray())
    bos.close()
    Log.d("test","$saved")
}

fun <T> readAny(key:String):T?
{
    var byte:ByteArray?=MMKV.defaultMMKV().decodeBytes(key)
    byte.let {

        try {
            var ois= ObjectInputStream(ByteArrayInputStream(byte))
            var obj:Any= ois.readObject()
            ois.close()
            return obj as T?
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    return null
}
fun saveData(key:String,pdata:Parcelable)
{
    var saved= MMKV.defaultMMKV().encode(key,pdata)
    Log.d("test","$saved")
}


fun <T : Parcelable> getCacheData(key: String, clazz: Class<T>): T?
{
    return MMKV.defaultMMKV().decodeParcelable(key, clazz)
}


fun clearCache(key:String)
{
    MMKV.defaultMMKV().removeValueForKey(key)
}