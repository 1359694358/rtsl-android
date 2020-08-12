package com.rt.rtsl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rt.rtsl.utils.StatusBarUtil
import com.rtsl.app.android.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusBarUtil.setLightMode(this)
    }
}