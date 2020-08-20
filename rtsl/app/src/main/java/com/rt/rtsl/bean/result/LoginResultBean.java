package com.rt.rtsl.bean.result;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.rt.rtsl.utils.UtilsKt;

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
    public static class LoginResult implements Parcelable
    {
        public static final String UserInfo="UserInfo";

        private static LoginResult loginResult;
        public static LoginResult getLoginResult()
        {
            synchronized (LoginResult.class)
            {
                if(loginResult==null)
                {
                    loginResult=UtilsKt.getCacheData(UserInfo,LoginResult.class);
                    if(loginResult==null)
                        loginResult=new LoginResult();
                }
                return loginResult;
            }
        }

        public static void setLoginResult(LoginResult value)
        {
            synchronized (LoginResult.class)
            {
                loginResult=value;
                if(value!=null)
                {
                    UtilsKt.saveData(UserInfo,value);
                }
                else
                {
                    UtilsKt.clearCache(UserInfo);
                }
            }
        }

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
        public YouZanTokenBean yzLoginResponse;

        public final boolean isLogin()
        {
            return !TextUtils.isEmpty(id);
        }

        public LoginResult(){}
        protected LoginResult(Parcel in) {
            id = in.readString();
            userId = in.readString();
            telephone = in.readString();
            wxId = in.readString();
            zfbId = in.readString();
            channelType = in.readString();
            nickName = in.readString();
            gender = in.readString();
            avatar = in.readString();
            accessToken = in.readString();
            verKey = in.readString();
            verCode = in.readString();
        }

        public static final Creator<LoginResult> CREATOR = new Creator<LoginResult>() {
            @Override
            public LoginResult createFromParcel(Parcel in) {
                return new LoginResult(in);
            }

            @Override
            public LoginResult[] newArray(int size) {
                return new LoginResult[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(userId);
            parcel.writeString(telephone);
            parcel.writeString(wxId);
            parcel.writeString(zfbId);
            parcel.writeString(channelType);
            parcel.writeString(nickName);
            parcel.writeString(gender);
            parcel.writeString(avatar);
            parcel.writeString(accessToken);
            parcel.writeString(verKey);
            parcel.writeString(verCode);
        }
    }
}
