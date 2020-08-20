package com.rt.rtsl.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.rt.rtsl.bean.request.LoginEntity
import com.rt.rtsl.bean.request.LoginType
import com.rt.rtsl.bean.result.LoginResultBean
import com.rt.rtsl.ui.widget.*
import com.rt.rtsl.utils.*
import com.rt.rtsl.vm.LoginViewModel
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityBindmobileBinding

class ActivityBindMobile: BaseActivity<ActivityBindmobileBinding>() {
    override fun getLayoutResId(): Int {
        return R.layout.activity_bindmobile
    }
    companion object
    {
        fun startActivity(context:Context,socialLoginEntity:LoginEntity)
        {
            var intent =Intent(context,ActivityBindMobile::class.java)
            intent.putExtra(Intent.ACTION_ATTACH_DATA,socialLoginEntity)
            context.startActivity(intent)
        }
    }
    lateinit var countDownTimer: CountDown
    val loginViewModel:LoginViewModel by lazy { getViewModelByApplication(LoginViewModel::class.java) }
    private var loginType=LoginType.Mobile
    private var verKey:String=""
    private var weChatId:String=""
    private var alipayId:String=""
    private var phone=""
    var socialLoginEntity:LoginEntity?=null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        socialLoginEntity=intent.getParcelableExtra(Intent.ACTION_ATTACH_DATA)
        setTitle(R.string.normaltitle)
        addLoginListener()
        countDownTimer = CountDown(60 * 1000)
    }

    fun addLoginListener()
    {
        contentBinding.smsCodeLogin.topTitle.text="手机号验证"
        contentBinding.smsCodeLogin.loginButton.text = "确定"
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
            loginType=LoginType.Mobile
            phone=contentBinding.smsCodeLogin.phoneInput.text.toString();
            var verCode=contentBinding.smsCodeLogin.smsCodeInput.text.toString()
            contentBinding.loadingView.visibility=View.VISIBLE
            socialLoginEntity?.verCode=verCode
            socialLoginEntity?.telephone=phone
            socialLoginEntity?.verKey=verKey
            loginViewModel.socialLoginBind(socialLoginEntity!!)
        }

        loginViewModel.loginObserver.observe(this, Observer
        {
            contentBinding.loadingView.visibility=View.GONE
            if(it?.yes()==true)
            {
                if(it.code==40002)
                {
                    ToastUtil.show(this,"绑定手机号失败 ${it?.msg?:""}")
                }
                else
                {
                    logd("绑定成功")
                    LoginResultBean.LoginResult.setLoginResult(it.data)
                    finish()
                }
            }
            else
            {
                ToastUtil.show(this,"绑定手机号失败 ${it?.msg?:""}")
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

        contentBinding.smsCodeLogin.phoneInput.requestFocus()

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
}