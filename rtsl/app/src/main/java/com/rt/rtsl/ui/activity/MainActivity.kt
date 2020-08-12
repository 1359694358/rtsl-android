package com.rt.rtsl.ui.activity

import android.os.Bundle
import com.rt.rtsl.utils.startActivity
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        contentBinding.root.postDelayed({
            startActivity(ActivityOneKeyLogin::class.java)
            finish()
        },2000)
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

}