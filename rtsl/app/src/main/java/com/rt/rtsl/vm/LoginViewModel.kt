package com.rt.rtsl.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rt.rtsl.bean.LoginResultBean
import com.rt.rtsl.bean.SmsCodeBean
import com.rt.rtsl.net.AppApi
import com.rt.rtsl.net.TransUtils

object LoginType
{
    const val Mobile=0
    const val WeChat=1
    const val Alipay=2
}
class LoginViewModel: ViewModel()
{

    val smsCodeObserver=MutableLiveData<SmsCodeBean?>()
    val loginObserver=MutableLiveData<LoginResultBean?>()
    fun getSmsCode(phone: String)
    {
        AppApi.serverApi.getSmsCode(phone).compose(TransUtils.jsonTransform(SmsCodeBean::class.java))
            .compose(TransUtils.schedulersTransformer())
            .subscribe(
                {
                    smsCodeObserver.postValue(it)
                }
                ,
                {
                    smsCodeObserver.postValue(null)
                }
            )
    }
    //telephone	否	string	用户手机号
//wx_id	否	string	微信id
//zfb_id	否	string	支付宝id
//channel_type	是	number	登录方式 0：手机； 1：微信；2：支付宝
//verKey	否	string	验证码标识，手机登录时
//verCode	否	string	验证码，手机登录时
    fun login(channel_type:Int=LoginType.Mobile,telephone:String="",verKey:String="",verCode:String="",wx_id:String="",zfb_id:String="")
    {

    }
}