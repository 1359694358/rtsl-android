package com.rt.rtsl.bean.request

import android.os.Parcel
import android.os.Parcelable

object LoginType
{
    const val Mobile=0
    const val WeChat=1
    const val Alipay=2
}
class LoginEntity(var channel_type:Int= LoginType.Mobile,var  telephone:String="",var  verKey:String="",var verCode:String="",var wx_id:String="",var zfb_id:String="",var nick_name:String?="",var avatar :String?= ""):BaseRequestBody(),Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(p: Parcel, p1: Int) {
        p.writeInt(channel_type)
        p.writeString(telephone)
        p.writeString(verKey)
        p.writeString(verCode)
        p.writeString(wx_id)
        p.writeString(zfb_id)
        p.writeString(nick_name)
        p.writeString(avatar)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginEntity> {
        override fun createFromParcel(parcel: Parcel): LoginEntity {
            return LoginEntity(parcel)
        }

        override fun newArray(size: Int): Array<LoginEntity?> {
            return arrayOfNulls(size)
        }
    }
}