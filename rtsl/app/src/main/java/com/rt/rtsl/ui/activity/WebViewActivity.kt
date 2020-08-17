package com.rt.rtsl.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.rt.rtsl.bean.result.LoginResultBean
import com.rt.rtsl.utils.ToastUtil
import com.rt.rtsl.utils.logd
import com.rt.rtsl.utils.startActivity
import com.rt.rtsl.vm.LoginViewModel
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityWebviewBinding
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension
import com.tencent.smtt.export.external.extension.proxy.X5ProxyWebViewClientExtension
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.youzan.androidsdk.YouzanSDK
import com.youzan.androidsdk.YouzanToken
import com.youzan.androidsdk.event.AbsAuthEvent
import com.youzan.androidsdk.event.AbsChooserEvent
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
    val loginViewMode:LoginViewModel by lazy { getViewModelByApplication(LoginViewModel::class.java) }
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
        contentBinding.toolbarBindingView.titleText.paint.isFakeBoldText=true
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

            override fun onReceivedTitle(p0: WebView?, p1: String?) {
                super.onReceivedTitle(p0, p1)
                p1?.let {
                    setTitle(it)
                }
            }
        })
       /* contentBinding.mView.setWebViewClient(object: WebViewClient()
        {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                contentBinding.mView.loadUrl(url)
                return true;
            }
        })*/
        if(TextUtils.isEmpty(url))
        {
            url=resources.getString(R.string.youzan_storeurl)
        }
//        url="https://www.baidu.com"
        contentBinding.mView.loadUrl(url)
        loginViewMode.youzanTokenObserver.observe(this, Observer {
            if(it?.yes()==true)
            {
                logd("登录成功 获取到有赞token")
                YouzanSDK.userLogout(this@WebViewActivity);
                //调用login接口, 获取数据, 组装成YouzanToken, 回传给SDK
                var token =  YouzanToken()
//                token.setAccessToken(it.data)
                token.setCookieKey(it.data.data.cookieKey)
                token.setCookieValue(it.data.data.cookieValue)

                // 这里注意调用顺序。先传递给sdk，再刷新view
                YouzanSDK.sync(getApplicationContext(), token);
                contentBinding.mView.loadUrl(url)
            }
            else
            {
                logd("获取到有赞token失败")
                contentBinding.mView.loadUrl(url)
            }
        })
        contentBinding.mView.subscribe(object : AbsChooserEvent() {
            override fun call(p0: Context?, p1: Intent?, p2: Int) {
                startActivityForResult(p1,p2)
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
//                        finish()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            contentBinding.mView.receiveFile(requestCode,data)
        }
    }

    override fun backHandle()
    {
        if(contentBinding.mView.pageCanGoBack()||contentBinding.mView.canGoBack())
        {
            contentBinding.mView.goBack();
        }
        else
        {
            var temp = System.currentTimeMillis()
            if (temp - time > Interval) {
                ToastUtil.show(this, "再按一次退出")
                time = temp
            } else {
                finish()
                //延时直接结束程序进程
                Handler().postDelayed({
                    killProcess()
                },200);
            }
        }
    }


    val Interval=2000
    var time = System.currentTimeMillis()-Interval
    override fun onBackPressed() {
       backHandle()
    }
}