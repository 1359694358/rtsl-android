package com.rt.rtsl.bean.result;

import android.os.Parcel;
import android.os.Parcelable;


public class YouZanTokenBean extends BaseResultBean<YouZanTokenBean.YouZanToken> implements Parcelable
{
    public YouZanTokenBean()
    {
    }

    protected YouZanTokenBean(Parcel in)
    {
        data=in.readParcelable(YouZanToken.class.getClassLoader());
    }

    public static final Creator<YouZanTokenBean> CREATOR = new Creator<YouZanTokenBean>() {
        @Override
        public YouZanTokenBean createFromParcel(Parcel in) {
            return new YouZanTokenBean(in);
        }

        @Override
        public YouZanTokenBean[] newArray(int size) {
            return new YouZanTokenBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(data,i);
    }

    public static class YouZanToken implements Parcelable
    {
        public String cookieKey;// : "open_cookie_e88624886b67c1085b"
        public String cookieValue;// : "YZ743926708960759808YZwE1oyRu7"
        public String yzOpenId;// : "9yINvkeT743843048374423552"

        public YouZanToken(){}
        protected YouZanToken(Parcel in) {
            cookieKey = in.readString();
            cookieValue = in.readString();
            yzOpenId = in.readString();
        }

        public static final Creator<YouZanToken> CREATOR = new Creator<YouZanToken>() {
            @Override
            public YouZanToken createFromParcel(Parcel in) {
                return new YouZanToken(in);
            }

            @Override
            public YouZanToken[] newArray(int size) {
                return new YouZanToken[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(cookieKey);
            parcel.writeString(cookieValue);
            parcel.writeString(yzOpenId);
        }
    }
}
