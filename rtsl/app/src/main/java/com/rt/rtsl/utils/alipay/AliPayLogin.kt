package com.rt.rtsl.utils.alipay

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.alipay.sdk.app.OpenAuthTask
import com.rt.rtsl.bean.request.AlipayAuthEntoty
import com.rt.rtsl.bean.result.AliPayAuthResultBean
import com.rt.rtsl.net.AppApi
import com.rt.rtsl.net.SSLSocketClient
import com.rt.rtsl.net.TransUtils
import com.rt.rtsl.utils.FileUtil
import com.rt.rtsl.utils.logd
import com.rtsl.app.android.R
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager


object AliPayLogin {
    /**
     * 通用跳转授权业务的回调方法。
     * 此方法在支付宝 SDK 中为弱引用，故你的 App 需要以成员变量等方式保持对 Callback 的强引用，
     * 以免对象被回收。
     * 以局部变量保持对 Callback 的引用是不可行的。
     */
    val openAuthCallback = OpenAuthTask.Callback { resultCode, memo, bundle ->
        var result= String.format(
                "业务成功，结果码: %s\n结果信息: %s\n结果数据: %s",
                resultCode,
                memo,
                bundleToString(bundle)
        )
        FileUtil.saveData(
                result,
                FileUtil.CACHE + "alipay_userinfo.txt"
        )

        if (resultCode == OpenAuthTask.OK) {
            Log.w(
                    "AliPay",
                    String.format(
                            "业务成功，结果码: %s\n结果信息: %s\n结果数据: %s",
                            resultCode,
                            memo,
                            bundleToString(bundle)
                    )
            )
            var resultJson=JSONObject(bundleToString(bundle))
            var auth_code:String=resultJson.optString("auth_code","")
            var app_id:String=resultJson.optString("app_id","")
            if(auth_code?.isNotEmpty())
            {
                autho_token(auth_code,app_id)
            }

        } else {
            Log.w(
                    "AliPay",
                    String.format(
                            "业务失败，结果码: %s\n结果信息: %s\n结果数据: %s",
                            resultCode,
                            memo,
                            bundleToString(bundle)
                    )
            )
        }
    }

    /**
     * 通用跳转授权业务 Demo
     */
    fun openAuthScheme(activity: Activity,resultCall:(AliPayAuthResultBean?)->Unit) {

        // 传递给支付宝应用的业务参数
        val bizParams: MutableMap<String, String> =
                HashMap()
        bizParams["url"] =
                "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=${activity.resources.getString(R.string.share_platform_alipayid)}&scope=auth_user,auth_base&state=init"
//        bizParams["url"] =
//            "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?auth_type=PURE_OAUTH_SD&app_id=${activity.resources.getString(R.string.share_platform_alipayid)}&scope=auth_user,auth_base"

        // 支付宝回跳到你的应用时使用的 Intent Scheme。请设置为不和其它应用冲突的值。
        // 请不要像 Demo 这样设置为 __alipaysdkdemo__!
        // 如果不设置，将无法使用 H5 中间页的方法(OpenAuthTask.execute() 的最后一个参数)回跳至
        // 你的应用。
        //
        // 注意！参见 AndroidManifest 中 <AlipayResultActivity> 的 android:scheme，此两处
        // 必须设置为相同的值。
        val scheme = activity.packageName

        // 唤起授权业务
        val task = OpenAuthTask(activity)
        task.execute(
                scheme,
                OpenAuthTask.BizType.AccountAuth,  // 业务类型
                bizParams,  // 业务参数
                OpenAuthTask.Callback { resultCode, memo, bundle ->
                    if (resultCode == OpenAuthTask.OK) {
                        var resultJson=JSONObject(bundleToString(bundle))
                        var auth_code:String=resultJson.optString("auth_code","")
                        if(auth_code?.isNotEmpty())
                        {
                            var alipayAuthEntoty=AlipayAuthEntoty(auth_code)
                            AppApi.serverApi.alipayAuth(alipayAuthEntoty).compose(TransUtils.schedulersTransformer())
                                    .compose(TransUtils.jsonTransform(AliPayAuthResultBean::class.java))
                                    .subscribe(
                                            {
                                                logd("$it")
                                                resultCall(it)
                                            }
                                            ,
                                            {
                                                logd("$it")
                                                resultCall(null)
                                            }
                                    )
                        }
                        else
                        {
                            resultCall(null)
                        }

                    } else {
                        resultCall(null)
                    }
                },  // 业务结果回调。注意：此回调必须被你的应用保持强引用
                true
        ) // 是否需要在用户未安装支付宝 App 时，使用 H5 中间页中转。建议设置为 true。
    }

    private fun bundleToString(bundle: Bundle?): String {
        if (bundle == null) {
            return "null"
        }
        val sb = StringBuilder()
        sb.append("{")
        val iterator=bundle.keySet().iterator()
        while (iterator.hasNext())
        {
            var key=iterator.next()
            sb.append("\"$key\"").append(":\"").append("${bundle[key]}\"")
            if(iterator.hasNext())
            {
                sb.append(",")
            }
        }
        sb.append("}")
        return sb.toString()
    }

    private fun autho_token(auth_code:String,app_id:String)
    {
        doAsync {
            try {
                var alipayAuthEntoty=AlipayAuthEntoty(auth_code)
                AppApi.serverApi.alipayAuth(alipayAuthEntoty)
                        .subscribe(
                                {
                                    logd("$it")
                                }
                                ,
                                {
                                    logd("$it")
                                }
                        )
                var okhttpBuilder=OkHttpClient.Builder()
                okhttpBuilder.readTimeout(AppApi.ReadTimeOut, TimeUnit.SECONDS)
                okhttpBuilder.connectTimeout(AppApi.ConnectTimeOut, TimeUnit.SECONDS)
                okhttpBuilder.sslSocketFactory(
                        SSLSocketClient.getSSLSocketFactory(),
                        SSLSocketClient.getTrustManager()[0] as X509TrustManager
                )
                var okhttpClient =okhttpBuilder.build()
                var df=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var requestrUrl="https://openapi.alipay.com/gateway.do"
                val builder = FormBody.Builder()
                        .add("app_id",app_id)
                        .add("method","alipay.system.oauth.token")
                        .add("charset","utf-8")
                        .add("sign_type","sign_type")
                        .add("timestamp",df.format(Date()))
                        .add("version","1.0")
                        .add("sign","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAotMPycKOV9cbA0WgXvuonBplPj99keuYOdFIIjx1rQutcMtFkt8PKIBkbCjyWQnfOxD5mTrAsYE5LiUUaHZykrogp6g07YeG4Qflwm0Xle5KhqtuDjy5dW2KW/PAWWj11M1EmHB97wxg5esf3pHUJDRBocGWA8ts4eenOkTcM6ROnoRVEg5v9o9/tm4r8kvmU8lr1CtdNTIeYviAYDFUIQ1JPfrfeJivIG+TRBpgu9LSfGEbdxylHtJKrulKQo741eFEpBUhEpM9NhhZ0aT3tJKOeqLknu3lmZUKzAGa21/uNQYVIaXvuqM270FTdhjDw6RawlSTV4I+JAUmNMwDswIDAQAB")
                        .add("grant_type", "authorization_code")
                        .add("code", auth_code)
                var request=Request.Builder().url(requestrUrl).post(builder.build()).build()
                var response=okhttpClient.newCall(request).execute()
                if(response!=null)
                {
                    response.body?.let {
                        var result=FileUtil.readTextInputStream(it.byteStream())
                    }
                }
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
    }
}