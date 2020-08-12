package com.rt.rtsl.bean.result;

public class LoginResultBean extends BaseResultBean<LoginResultBean.LoginResult>{

//    "id": 3,
//        "userId": null,
//        "telephone": "19136070747",
//        "wxId": null,
//        "zfbId": null,
//        "channelType": 0,
//        "nickName": null,
//        "gender": 0,
//        "avatar": null,
//        "accessToken": null,
//        "verKey": "7e4e131c-dffa-4779-8c44-a76641a6ad5c",
//        "verCode": "970601"
    public static class LoginResult
    {
        public String id;
        public String userId;
        public String telephone;
        public String wxId;
        public String zfbId;
        public String channelType;
        public String nickName;
        public String gender;
        public String avatar;
        public String accessToken;
        public String verKey;
        public String verCode;
    }
}
