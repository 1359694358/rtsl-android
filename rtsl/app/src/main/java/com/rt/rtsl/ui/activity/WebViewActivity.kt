package com.rt.rtsl.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityWebviewBinding
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
    override fun getLayoutResId(): Int
    {
        return R.layout.activity_webview
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        var url=intent.getStringExtra(WebUrl)
        if(TextUtils.isEmpty(url))
        {
            url=resources.getString(R.string.youzan_storeurl)
        }
        contentBinding.mView.loadUrl(url)
    }
}