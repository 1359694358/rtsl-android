package com.rt.rtsl.ui.activity

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.request.ExplainScope
import com.rt.rtsl.ui.widget.*
import com.rt.rtsl.utils.PermissionPageUtils
import com.rt.rtsl.utils.ToastUtil
import com.rt.rtsl.utils.Utility
import com.rt.rtsl.utils.logd
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityOnekeyloginBinding
import com.rtsl.app.android.databinding.SimpleDialogBinding

class ActivityOneKeyLogin: BaseActivity<ActivityOnekeyloginBinding>() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_onekeylogin
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setTitle(R.string.normaltitle)
        requestReadPhoneNumberPermission()
    }

    fun requestReadPhoneNumberPermission()
    {
        PermissionX.init(this).permissions(Manifest.permission.READ_PHONE_STATE)
            .onExplainRequestReason { scope, deniedList ->
                logd("onExplainRequestReason")
                var simpleDialogBinding=SimpleDialogBinding.inflate(layoutInflater)
                simpleDialogBinding.setSubTitle("${resources.getString(R.string.app_name)}需要读取您的电话号码用于一键登录")
                simpleDialogBinding.setCancelClickListener {
                    ToastUtil.show(this,"没有取到权限，无法登录")
                }
                simpleDialogBinding.setSureClickListener {
                    requestReadPhoneNumberPermission()
                }
                simpleDialogBinding.show(window.decorView as ViewGroup)
            }
            .onForwardToSettings { scope, deniedList ->
                logd("onForwardToSettings")
                var simpleDialogBinding=SimpleDialogBinding.inflate(layoutInflater)
                simpleDialogBinding.setSubTitle("您已禁止${resources.getString(R.string.app_name)}访问读取手机号权限，现无法一键登录。可以点击确认进入设置页面，授予对应权限")
                simpleDialogBinding.setCancelClickListener {
                    ToastUtil.show(this,"没有取到权限，无法登录")
                }
                simpleDialogBinding.setSureClickListener {
                    var permissionPage= PermissionPageUtils(this)
                    permissionPage.jumpPermissionPage()
                }
                simpleDialogBinding.show(window.decorView as ViewGroup)
            }
            .request { allGranted, grantedList, deniedList ->
                logd("allGranted:$allGranted")
                if(allGranted)
                {
                    var phone=Utility.getPhoneNumber(this)
                    contentBinding.phoneNumber.text=phone
                    if(TextUtils.isEmpty(phone))
                    {
                        ToastUtil.show(this,"获取手机号失败，请切换手机号登录")
                    }
                }
                else
                {
                    contentBinding.phoneNumber.text=""
                }
            }
    }
}