package com.rt.rtsl.utils.alipay

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.alipay.sdk.app.OpenAuthTask
import com.rt.rtsl.utils.FileUtil
import com.rtsl.app.android.R
import java.util.*

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

            val alipayClient: AlipayClient = DefaultAlipayClient(
                "https://openapi.alipay.com/gateway.do",
                "app_id",
                "your private_key",
                "json",
                "GBK",
                "alipay_public_key",
                "RSA2"
            )
            val request = AlipaySystemOauthTokenRequest()
            request.setGrantType("authorization_code")
            request.setCode("4b203fe6c11548bcabd8da5bb087a83b")
            request.setRefreshToken("201208134b203fe6c11548bcabd8da5bb087a83b")
            val response: AlipaySystemOauthTokenResponse = alipayClient.execute(request)
            if (response.isSuccess()) {
                println("调用成功")
            } else {
                println("调用失败")
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
    fun openAuthScheme(activity: Activity) {

        // 传递给支付宝应用的业务参数
        val bizParams: MutableMap<String, String> =
            HashMap()
        bizParams["url"] =
            "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=${activity.resources.getString(R.string.share_platform_alipayid)}&scope=auth_user&state=init"

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
            openAuthCallback,  // 业务结果回调。注意：此回调必须被你的应用保持强引用
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
            sb.append("\"$key\"").append(":").append(bundle[key])
            if(iterator.hasNext())
            {
                sb.append(",")
            }
        }
        sb.append("}")
        return sb.toString()
    }
}