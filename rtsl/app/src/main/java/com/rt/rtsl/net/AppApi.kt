package com.rt.rtsl.net

import com.rt.rtsl.bean.request.AlipayAuthEntoty
import com.rt.rtsl.bean.request.LoginEntity
import com.rt.rtsl.bean.request.SmsCodeEntity
import com.rt.rtsl.bean.request.YouZanSysEntity
import com.rt.rtsl.utils.ExceptionHandler
import com.rtsl.app.android.BuildConfig
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager
import io.reactivex.Observable
import okhttp3.Request
import org.json.JSONObject
import retrofit2.http.*

interface ServerApi
{
    @POST("api/ap_user/sendSms")
    fun getSmsCode(@Body phoneNumber:SmsCodeEntity):Observable<JSONObject>

    @POST("/api/ap_user/login")
    fun login(@Body loginEntity: LoginEntity):Observable<JSONObject>

    @POST("api/ap_user/yzLogin")
    fun sysYouZanUser(@Body youZanSysEntity: YouZanSysEntity):Observable<JSONObject>

    @POST("api/ap_user/alipayAuth")
    fun alipayAuth(@Body authEntoty: AlipayAuthEntoty):Observable<JSONObject>

    @POST("api/ap_user/bindPhone")
    fun socialLoginBind(@Body loginEntity: LoginEntity):Observable<JSONObject>

    @GET("api/ap_user/getAppVersion")
    fun checkUpdate():Observable<JSONObject>
}

object AppApi
{
    var ReadTimeOut = 5L
    var ConnectTimeOut = 5L
    //服务器接口地址修改就改这
//    val Host="https://m.runtae.com"
//    val Host="http://www.huimiao.wang"
    val serverApi:ServerApi
    init {
        RxJavaPlugins.setErrorHandler { throwable ->
            ExceptionHandler.getInstance().saveCrashInfo2File(throwable)
        }
        val okhttpBuilder = OkHttpClient.Builder()
        okhttpBuilder.addInterceptor {
            val original: Request = it.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .addHeader("Content-Type", "application/json")
            val request: Request = requestBuilder.build()
            return@addInterceptor it.proceed(request)
        }
        okhttpBuilder.readTimeout(ReadTimeOut, TimeUnit.SECONDS)
        okhttpBuilder.connectTimeout(ConnectTimeOut, TimeUnit.SECONDS)
        okhttpBuilder.sslSocketFactory(
            SSLSocketClient.getSSLSocketFactory(),
            SSLSocketClient.getTrustManager()[0] as X509TrustManager
        )
        okhttpBuilder.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
        val okHttpClient = okhttpBuilder.build()
        val retrofit= Retrofit.Builder()
            .baseUrl(BuildConfig.Host)
            .addConverterFactory(JSONObjectConvertFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        serverApi=retrofit.create(ServerApi::class.java)
    }

}