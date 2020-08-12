package com.rt.rtsl.bean.request

object LoginType
{
    const val Mobile=0
    const val WeChat=1
    const val Alipay=2
}
class LoginEntity(var channel_type:Int= LoginType.Mobile,var  telephone:String="",var  verKey:String="",var verCode:String="",var wx_id:String="",var zfb_id:String=""):BaseRequestBody()