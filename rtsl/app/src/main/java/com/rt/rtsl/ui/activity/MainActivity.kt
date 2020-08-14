package com.rt.rtsl.ui.activity

import android.Manifest
import android.os.Bundle
import android.view.ViewGroup
import com.permissionx.guolindev.PermissionX
import com.rt.rtsl.App
import com.rt.rtsl.ui.widget.*
import com.rt.rtsl.utils.PermissionPageUtils
import com.rt.rtsl.utils.ToastUtil
import com.rt.rtsl.utils.startActivity
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityMainBinding
import com.rtsl.app.android.databinding.SimpleDialogBinding
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        requestAppPermission()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        killProcess()
    }
    fun requestAppPermission()
    {
        var sdCard= listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
            .onExplainRequestReason { scope, deniedList ->
                if(deniedList.containsAll(sdCard))
                {
                    var simpleDialogBinding=SimpleDialogBinding.inflate(layoutInflater)
                    simpleDialogBinding.setSubTitle("${resources.getString(R.string.app_name)}需要读取您的存储卡，以便于数据存储")
                    simpleDialogBinding.setCancelClickListener {
                        ToastUtil.show(this,"没有取到权限")
                    }
                    simpleDialogBinding.setSureClickListener {
                        requestAppPermission()
                    }
                    simpleDialogBinding.show(window.decorView as ViewGroup)
                }
                else
                {
                    init()
                }

            }
            .onForwardToSettings { _, deniedList ->
                if(deniedList.containsAll(sdCard))
                {
                    var simpleDialogBinding=SimpleDialogBinding.inflate(layoutInflater)
                    simpleDialogBinding.setSubTitle("您已禁止${resources.getString(R.string.app_name)}访问访问存储权限。可以点击确认进入设置页面，授予对应权限")
                    simpleDialogBinding.setCancelClickListener {
                        ToastUtil.show(this,"没有取到权限")
                    }
                    simpleDialogBinding.setSureClickListener {
                        var permissionPage= PermissionPageUtils(this)
                        permissionPage.jumpPermissionPage()
                    }
                    simpleDialogBinding.show(window.decorView as ViewGroup)
                }
                else
                {
                    init()
                }

            }
            .request { allGranted, grantedList, _ ->
                if(allGranted||grantedList.containsAll(sdCard))
                {
                    init()
                }
                else
                {
                    var simpleDialogBinding=SimpleDialogBinding.inflate(layoutInflater)
                    simpleDialogBinding.setSubTitle("您已禁止${resources.getString(R.string.app_name)}授权权限。可以点击确认进入设置页面，授予对应权限")
                    simpleDialogBinding.setCancelClickListener {
                        killProcess()
                    }
                    simpleDialogBinding.setSureClickListener {
                        var permissionPage= PermissionPageUtils(this)
                        permissionPage.jumpPermissionPage()
                    }
                    simpleDialogBinding.show(window.decorView as ViewGroup)
                }
            }
    }
    private fun init()
    {
        App.initFile(this)
        startLoginActivity()
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    fun startLoginActivity()
    {
        contentBinding.root.postDelayed({
            startActivity(ActivityLogin::class.java)
            finish()
        },2000)
    }

}