package com.rt.rtsl.bean.result;
//{
//        "code": 0,
//        "data": {
//        "verKey": "1b8c42a4-0dc0-4b5f-9e21-8dfb9ae11251"
//        },
//        "msg": null
//        }
//code	string	大于0表示正常
//        verKey	string	验证码标识，登录时需传回服务端
public class SmsCodeBean extends BaseResultBean<SmsCodeBean.SmsData>
{
    public static class SmsData
    {
        public String verKey;
    }
}
