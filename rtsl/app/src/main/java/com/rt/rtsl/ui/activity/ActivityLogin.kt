package com.rt.rtsl.ui.activity

import android.Manifest
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.permissionx.guolindev.PermissionX
import com.rt.rtsl.ui.widget.*
import com.rt.rtsl.bean.request.LoginType
import com.rt.rtsl.utils.*
import com.rt.rtsl.vm.LoginViewModel
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityLoginBinding
import com.rtsl.app.android.databinding.SimpleDialogBinding

class ActivityLogin: BaseActivity<ActivityLoginBinding>() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }
    lateinit var countDownTimer: CountDown
    val loginViewModel:LoginViewModel by lazy { getViewModel(LoginViewModel::class.java) }
    private var loginType=LoginType.Mobile
    private var verKey:String=""
    private var weChatId:String=""
    private var alipayId:String=""
    private var phone=""
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setTitle(R.string.normaltitle)
//        requestReadPhoneNumberPermission()
        contentBinding.oneKeyLogin.loginSmsLayoutSwitch.setOnClickListener {
            contentBinding.oneKeyLogin.root.visibility= View.GONE
            contentBinding.smsCodeLogin.root.visibility=View.VISIBLE
        }
        addLoginListener()
        countDownTimer = CountDown(60 * 1000)
    }

    fun addLoginListener()
    {
        contentBinding.smsCodeLogin.getSmsCode.setOnClickListener {
            var phone=contentBinding.smsCodeLogin.phoneInput.text.toString()
            if(!Utility.netstatusOk(this))
            {
                ToastUtil.show(this,"请检查你的网络")
                return@setOnClickListener
            }
            if(!Utility.isMobileNO(phone))
            {
                ToastUtil.show(it.context,"请输入正确的手机号")
                return@setOnClickListener
            }
            invokeGetInvalidataNum()
        }
        contentBinding.smsCodeLogin.loginButton.setOnClickListener {
            if(!Utility.netstatusOk(this))
            {
                ToastUtil.show(this,"请检查你的网络")
                return@setOnClickListener
            }
            if(!Utility.isMobileNO(contentBinding.smsCodeLogin.phoneInput.text.toString()))
            {
                ToastUtil.show(it.context,"请输入正确的手机号")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(contentBinding.smsCodeLogin.smsCodeInput.text.toString()))
            {
                ToastUtil.show(it.context,"请输入验证码")
                return@setOnClickListener
            }
            phone=contentBinding.smsCodeLogin.phoneInput.text.toString();
            var verCode=contentBinding.smsCodeLogin.smsCodeInput.text.toString()
            loginViewModel.login(loginType,phone,verKey,verCode,weChatId,alipayId)
        }

        loginViewModel.loginObserver.observe(this, Observer {
            if(it?.yes()==true)
            {
                ToastUtil.show(this,"登录成功")
                logd("登录成功")
            }
            else
            {
                ToastUtil.show(this,"登录失败 ${it?.msg?:""}")
            }
        })
        loginViewModel.smsCodeObserver.observe(this, Observer{
            if(it?.yes()==true)
            {
                countDownTimer.start()
                verKey=it.data.verKey
                ToastUtil.show(this,"验证码已发送")
            }
            else
            {
                ToastUtil.show(this,"验证码发送失败 ${it?.msg?:""}")
                resetGetInvalidataBtn()
            }
        })

        contentBinding.bottomLayout.gov2.setOnClickListener {
            var use_terms=resources.getString(R.string.use_terms)
            var path="${FileUtil.CACHE}${use_terms}"
            OfficeFileViewActivity.startActivity(it.context,path,contentBinding.bottomLayout.gov2.text.toString())
        }

        contentBinding.bottomLayout.gov4.setOnClickListener {
            var app_policy=resources.getString(R.string.app_policy)
            var path="${FileUtil.CACHE}${app_policy}"
            OfficeFileViewActivity.startActivity(it.context,path,contentBinding.bottomLayout.gov4.text.toString())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backHandle()
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
                    contentBinding.oneKeyLogin.phoneNumber.text=phone
                    if(TextUtils.isEmpty(phone))
                    {
                        ToastUtil.show(this,"获取手机号失败，请切换手机号登录")
                    }
                    else
                    {
                        contentBinding.oneKeyLogin.loginButton.setOnClickListener {

                        }
                    }
                }
                else
                {
                    contentBinding.oneKeyLogin.phoneNumber.text=""
                }
            }
    }
    inner class CountDown(millisInFuture: Long) : CountDownTimer(millisInFuture, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            val remain = (millisUntilFinished / 1000).toInt()
            val label = resources.getString(R.string.reget)
            contentBinding.smsCodeLogin.getSmsCode.text = remain.toString() + label
        }

        override fun onFinish() {
            resetGetInvalidataBtn()
        }

    }

    fun invokeGetInvalidataNum()
    {
        hideKeyBroad()
        contentBinding.smsCodeLogin.getSmsCode.text = "发送中"
        contentBinding.smsCodeLogin.getSmsCode.isClickable = false
        loginViewModel.getSmsCode(contentBinding.smsCodeLogin.phoneInput.text.toString())
//        countDownTimer.start()
    }

    fun resetGetInvalidataBtn() {
        contentBinding.smsCodeLogin.getSmsCode.setText(R.string.getvalidatenum)
        if (Utility.isMobileNO(contentBinding.smsCodeLogin.phoneInput.text.toString()))
        {
            contentBinding.smsCodeLogin.getSmsCode.isEnabled = true
            contentBinding.smsCodeLogin.getSmsCode.isClickable = true
        }

    }

    override fun backHandle() {
        //延时直接结束程序进程
       Handler().postDelayed({
           killProcess()
       },1000);
    }

}