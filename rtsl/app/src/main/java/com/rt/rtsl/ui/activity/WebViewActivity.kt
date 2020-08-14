package com.rt.rtsl.ui.activity

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.rt.rtsl.bean.result.LoginResultBean
import com.rt.rtsl.utils.logd
import com.rt.rtsl.utils.startActivity
import com.rt.rtsl.vm.LoginViewModel
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityWebviewBinding
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.youzan.androidsdk.YouzanSDK
import com.youzan.androidsdk.YouzanToken
import com.youzan.androidsdk.event.AbsAuthEvent
import org.jetbrains.anko.startActivity


class WebViewActivity: BaseActivity<ActivityWebviewBinding>()
{
    companion object
    {
        const val WebUrl="webUrl"
        fun startActivity(context:Context,url:String="")
        {
            context.startActivity<WebViewActivity>(Pair(WebUrl,url))
        }
    }
    val loginViewMode:LoginViewModel by lazy { getViewModel(LoginViewModel::class.java) }
    override fun getLayoutResId(): Int
    {
        return R.layout.activity_webview
    }
    var url:String?=""
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        url=intent.getStringExtra(WebUrl)

        addObsever()
        sysUserToken()
    }

    fun sysUserToken(): Boolean {
        var loginBean=LoginResultBean.LoginResult.getLoginResult()
        if(loginBean.isLogin) {
            loginViewMode.getYouZanToken(
                loginBean.id,
                resources.getString(R.string.youzan_clientId)
            )
            return true
        }
        return false
    }

    fun addObsever()
    {
        contentBinding.mView.setWebChromeClient(object : WebChromeClient() {
            override fun onShowCustomView(
                view: View?,
                customViewCallback: IX5WebChromeClient.CustomViewCallback
            ) {
                super.onShowCustomView(view, customViewCallback)
                customViewCallback.onCustomViewHidden()
            }
        })
        contentBinding.mView.setWebViewClient(object: WebViewClient()
        {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                contentBinding.mView.loadUrl(url)
                return true;
            }
        })
        if(TextUtils.isEmpty(url))
        {
            url=resources.getString(R.string.youzan_storeurl)
        }
        url="https://www.baidu.com"
        contentBinding.mView.loadUrl(url)
        loginViewMode.youzanTokenObserver.observe(this, Observer {
            if(it?.yes()==true)
            {
                logd("登录成功 获取到有赞token")
                YouzanSDK.userLogout(this@WebViewActivity);
                //调用login接口, 获取数据, 组装成YouzanToken, 回传给SDK
                var token =  YouzanToken()
                token.setAccessToken(it.data.yzOpenId)
                token.setCookieKey(it.data.cookieKey)
                token.setCookieValue(it.data.cookieValue)

                // 这里注意调用顺序。先传递给sdk，再刷新view
                YouzanSDK.sync(getApplicationContext(), token);
//                contentBinding.mView.loadUrl(url)
                contentBinding.mView.sync(token);
            }
            else
            {
                logd("获取到有赞token失败")
                contentBinding.mView.loadUrl(url)
            }
        })
        contentBinding.mView.subscribe(object :AbsAuthEvent()
        {
            override fun call( context:Context, needLogin:Boolean )
            {
                if(needLogin)
                {
                    if(!sysUserToken())
                    {
                        YouzanSDK.userLogout(this@WebViewActivity);
                        startActivity(ActivityLogin::class.java)
                        finish()
                    }
                }
            }
        })
    }
}