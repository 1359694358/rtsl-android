package com.rt.rtsl.bean.request

import com.rt.rtsl.net.TransUtils


open class BaseRequestBody()
{
    override fun toString(): String
    {
        return TransUtils.gson.toJson(this)
    }
}