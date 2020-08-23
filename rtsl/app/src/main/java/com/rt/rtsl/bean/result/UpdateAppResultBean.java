package com.rt.rtsl.bean.result;

import com.google.gson.annotations.SerializedName;

public class UpdateAppResultBean extends BaseResultBean<UpdateAppResultBean.UpdateInfo> {
    public class UpdateInfo
    {
        @SerializedName("android-version")
        public String version;
        public String url;
        public String isForced;
    }
}
