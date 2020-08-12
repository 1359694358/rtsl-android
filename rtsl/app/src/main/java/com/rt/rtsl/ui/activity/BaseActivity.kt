package com.rt.rtsl.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rt.rtsl.utils.StatusBarUtil
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.AppToolbarBinding

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity()
{
    lateinit var contentBinding:T
    var toolbarBinding:AppToolbarBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentBinding=DataBindingUtil.inflate(layoutInflater,getLayoutResId(),null,false)
        setContentView(contentBinding.root)
        setStatusBarMode()
        setStatusBarColor()
        var toolbarBindingView: View?=contentBinding.root.findViewById(R.id.toolbarBindingView)
        if(toolbarBindingView!=null)
        {
            toolbarBinding=DataBindingUtil.findBinding(toolbarBindingView)
            toolbarBinding?.backBtn?.setOnClickListener { finish() }
        }
    }

    fun setTitle(title:String)
    {
        toolbarBinding?.titleText?.text=title
    }

    override fun setTitle(titleId: Int)
    {
        toolbarBinding?.titleText?.setText(titleId)
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