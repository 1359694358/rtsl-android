package com.rt.rtsl.ui.activity

import android.os.Bundle
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityWebviewBinding

class WebViewActivity: BaseActivity<ActivityWebviewBinding>() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_webview
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

    }
}