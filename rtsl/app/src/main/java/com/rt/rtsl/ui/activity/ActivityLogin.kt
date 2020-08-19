package com.rt.rtsl.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.mediacloud.app.share.SocialLoginControl
import com.mediacloud.app.share.socialuserinfo.SocialUserInfo
import com.permissionx.guolindev.PermissionX
import com.qmuiteam.qmui.widget.QMUILoadingView
import com.rt.rtsl.bean.request.LoginType
import com.rt.rtsl.bean.result.LoginResultBean
import com.rt.rtsl.ui.widget.*
import com.rt.rtsl.utils.*
import com.rt.rtsl.utils.alipay.AliPayLogin
import com.rt.rtsl.vm.LoginViewModel
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityLoginBinding
import com.rtsl.app.android.databinding.SimpleDialogBinding
import com.umeng.socialize.bean.SHARE_MEDIA

class ActivityLogin: BaseActivity<ActivityLoginBinding>(), SocialLoginControl.SocialLoginListener {
    override fun getLayoutResId(): Int {
        return R.layout.activity_login
    }
    lateinit var countDownTimer: CountDown
    val loginViewModel:LoginViewModel by lazy { getViewModelByApplication(LoginViewModel::class.java) }
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
        SocialLoginControl.addSocialLoginListener(this)
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
            loginType=LoginType.Mobile
            phone=contentBinding.smsCodeLogin.phoneInput.text.toString();
            var verCode=contentBinding.smsCodeLogin.smsCodeInput.text.toString()
            contentBinding.loadingView.visibility=View.VISIBLE
            loginViewModel.login(resources.getString(R.string.youzan_clientId),phone,"",loginType,phone,verKey,verCode,weChatId,alipayId)
        }

        loginViewModel.loginObserver.observe(this, Observer
        {
            contentBinding.loadingView.visibility=View.GONE
            if(it?.yes()==true)
            {
                logd("登录成功")
                LoginResultBean.LoginResult.setLoginResult(it.data)
                finish()
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
            var cacheDir=cacheDir.absolutePath
            var path="${cacheDir}${use_terms}"
            OfficeFileViewActivity.startActivity(it.context,path,contentBinding.bottomLayout.gov2.text.toString())
        }

        contentBinding.bottomLayout.gov4.setOnClickListener {
            var app_policy=resources.getString(R.string.app_policy)
            var cacheDir=cacheDir.absolutePath
            var path="${cacheDir}${app_policy}"
            OfficeFileViewActivity.startActivity(it.context,path,contentBinding.bottomLayout.gov4.text.toString())
        }
        contentBinding.smsCodeLogin.phoneInput.requestFocus()


        contentBinding.bottomLayout.loginByWeChat.setOnClickListener {
            loginType=LoginType.WeChat
            SocialLoginControl.socialdoOauthVerify(SHARE_MEDIA.WEIXIN,this)
            contentBinding.loadingView.visibility=View.VISIBLE
        }
        contentBinding.bottomLayout.loginByAliPay.setOnClickListener {
            contentBinding.loadingView.visibility=View.VISIBLE
            loginType=LoginType.Alipay
            AliPayLogin.openAuthScheme(this)
            {
                if(it!=null&&it.data?.user_id?.isNotEmpty()==true)
                {
                    alipayId=it.data.user_id
                    var verCode=""
                    logd("支付宝授权成功 ${it.data.user_id}")
//                    loginViewModel.login(resources.getString(R.string.youzan_clientId),it.data.nick_name,it.data.avatar,loginType,phone,verKey,verCode,weChatId,alipayId)
                    startActivity(ActivityBindMobile::class.java)
                    finish()
                }
                else
                {
                    ToastUtil.show(this,"支付宝登录失败")
                    contentBinding.loadingView.visibility=View.GONE
                }
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

    override fun getSocialuserInfoError() {
        logd("loginComplete")
        ToastUtil.show(this,"获取用户信息失败")
    }

    override fun loginComplete(p0: MutableMap<String, String>?, p1: SHARE_MEDIA?)
    {
        logd("loginComplete")
    }

    override fun loginError(p0: String?, p1: SHARE_MEDIA?)
    {
        logd("loginError")
        ToastUtil.show(this,"授权登录失败")
    }

    override fun getSocialUserInfoComplete(p0: SocialUserInfo?)
    {
        logd("getSocialUserInfoComplete")
        if(p0!=null)
        {
            weChatId=p0.uid
           /* var verCode=""
            loginViewModel.login(resources.getString(R.string.youzan_clientId),p0.getNickName(),p0.getHeadURL(),loginType,phone,verKey,verCode,weChatId,alipayId)*/

            var mobileInfo=readAny<String>(weChatId)

            if(mobileInfo?.isNotEmpty() == true)
            {

            }
            else
            {
                startActivity(ActivityBindMobile::class.java)
                finish()
            }
        }
        else
        {
            ToastUtil.show(this,"获取用户信息失败")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SocialLoginControl.onActivityResult(requestCode, resultCode, data)
    }
}