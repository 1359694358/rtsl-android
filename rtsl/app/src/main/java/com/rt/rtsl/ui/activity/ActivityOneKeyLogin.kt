package com.rt.rtsl.ui.activity

import android.os.Bundle
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityOnekeyloginBinding

class ActivityOneKeyLogin: BaseActivity<ActivityOnekeyloginBinding>() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_onekeylogin
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setTitle(R.string.normaltitle)
    }
}