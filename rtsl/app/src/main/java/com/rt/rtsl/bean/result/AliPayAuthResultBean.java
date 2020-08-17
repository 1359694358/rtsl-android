package com.rt.rtsl.bean.result;

/**
 * 支付宝的授权
 */
public class AliPayAuthResultBean extends BaseResultBean<AliPayAuthResultBean.AliPayUserBean> {

    public class AliPayUserBean
    {
        public String user_id;// : "2088702201413705"
        public String access_token;// : "composeBa8d0fb003f91422dac9ab1009316dX70"
        public String avatar;
        public String nick_name;
    }
}
