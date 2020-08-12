package com.rt.rtsl.bean.result;

public class BaseResultBean<T> {
    //code	string	大于0表示正常
    public int code;
    public String msg;
    public T data;

    public boolean yes() {
        return code>=0;
    }
}
