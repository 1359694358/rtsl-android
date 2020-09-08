package com.rt.rtsl.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.rt.rtsl.bean.result.LoginResultBean
import com.rt.rtsl.utils.*
import com.rt.rtsl.vm.LoginViewModel
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityWebviewBinding
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.youzan.androidsdk.YouzanSDK
import com.youzan.androidsdk.YouzanToken
import com.youzan.androidsdk.event.AbsAuthEvent
import com.youzan.androidsdk.event.AbsChooserEvent
import com.youzan.androidsdk.event.AbsShareEvent
import com.youzan.androidsdk.model.goods.GoodsShareModel
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
        PgyUpdate.updateCheck(this)
        contentBinding.toolbarBindingView.loginOut.setOnClickListener {
            it.visibility = View.GONE
            YouzanSDK.userLogout(this@WebViewActivity)
            contentBinding.mView.loadUrl(resources.getString(R.string.youzan_storeurl))
        }
    }


    fun sysUserToken(): Boolean {
       /* var loginBean=LoginResultBean.LoginResult.getLoginResult()
        if(loginBean.isLogin) {
            loginViewMode.getYouZanToken(
                    loginBean.id,
                    resources.getString(R.string.youzan_clientId),
                    loginBean.nickName,
                    loginBean.avatar,
                    loginBean.telephone
            )
            return true
        }*/
        return false
    }

    @SuppressLint("CheckResult")
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
                    if(it == "会员主页"){
                        contentBinding.toolbarBindingView.loginOut.visibility = View.VISIBLE
                    }else{
                        contentBinding.toolbarBindingView.loginOut.visibility = View.GONE
                    }
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
                token.cookieKey = it.data.cookieKey
                token.cookieValue = it.data.cookieValue
                // 这里注意调用顺序。先传递给sdk，再刷新view
                YouzanSDK.sync(applicationContext, token);
                contentBinding.mView.sync(token)
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

        contentBinding.mView.subscribe(object : AbsShareEvent() {
            override fun call(p0: Context?, p1: GoodsShareModel?) {
                Glide.with(this@WebViewActivity).asBitmap().listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        WxShareUtils.INSTANCE.shareWebPage(
                            this@WebViewActivity,
                            SendMessageToWX.Req.WXSceneSession,
                            p1?.link,
                            p1?.title,
                            p1?.desc,
                            null
                        )
                        return true
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        WxShareUtils.INSTANCE.shareWebPage(
                            this@WebViewActivity,
                            SendMessageToWX.Req.WXSceneSession,
                            p1?.link,
                            p1?.title,
                            p1?.desc,
                            resource
                        )
                        return true
                    }

                })
                    .load(p1?.imgUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .submit()
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
                        LoginResultBean.LoginResult.setLoginResult(null)
                        startActivity(ActivityLogin::class.java)
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


    val Interval=3000
    var time = System.currentTimeMillis()-Interval
    override fun onBackPressed() {
        backHandle()
    }
}