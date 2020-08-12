package com.rt.rtsl.ui.widget

import android.view.ViewGroup
import androidx.annotation.StringRes
import com.rtsl.app.android.databinding.SimpleDialogBinding

fun SimpleDialogBinding.show(parent:ViewGroup)
{
    parent.addView(this.root)
}

fun SimpleDialogBinding.dissmiss()
{
    if(this.root.parent!=null)
    {
        (this.root.parent as ViewGroup).removeView(this.root)
    }
}

fun SimpleDialogBinding.setMainTitle(value:String)
{
    title1.text = value
}

fun SimpleDialogBinding.setMainTitle(@StringRes value:Int)
{
    title1.setText(value)
}

fun SimpleDialogBinding.setSubTitle(value:String)
{
    title2.text = value
}

fun SimpleDialogBinding.setSubTitle(@StringRes value:Int)
{
    title2.setText(value)
}

fun SimpleDialogBinding.setSummary(value:String)
{
    summary.text = value
}

fun SimpleDialogBinding.setSummary(@StringRes value:Int)
{
    summary.setText(value)
}

fun SimpleDialogBinding.setSureClickListener(call:()->Unit)
{
    sure.setOnClickListener {
        call()
        dissmiss()
    }
}

fun SimpleDialogBinding.setCancelClickListener(call:()->Unit)
{
    cancel.setOnClickListener {
        call()
        dissmiss()
    }
}