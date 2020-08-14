package com.rt.rtsl.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Process
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
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
            toolbarBinding?.backBtn?.setOnClickListener {
                backHandle()
            }
        }
    }

    protected open fun backHandle()
    {
        finish()
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

    protected fun <T: ViewModel> getViewModel(modelClazz:Class<T>): T
    {
        return ViewModelProviders.of(this).get(modelClazz)
    }

    override fun finish() {
        hideKeyBroad()
        super.finish()
    }
    protected fun hideKeyBroad()
    {
        val v = window.peekDecorView()
        if (v != null && v.windowToken != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    protected fun showKeyBroad(view:View)
    {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view,0)
    }
    protected final fun killProcess()
    {
        Process.killProcess(Process.myPid())
    }
}