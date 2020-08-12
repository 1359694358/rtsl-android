package com.rt.rtsl.ui.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rt.rtsl.utils.StatusBarUtil
import com.rtsl.app.android.R

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity()
{
    lateinit var contentBinding:T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentBinding=DataBindingUtil.inflate(layoutInflater,getLayoutResId(),null,false)
        setContentView(contentBinding.root)
        setStatusBarMode()
        setStatusBarColor()
    }
    fun setStatusBarMode()
    {
        StatusBarUtil.setLightMode(this)
    }

    fun setStatusBarColor()
    {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.statusbar_color),0)
    }
    abstract fun getLayoutResId():Int
}