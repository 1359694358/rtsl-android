package com.rt.rtsl.net

import com.rt.rtsl.bean.request.LoginEntity
import com.rt.rtsl.bean.request.SmsCodeEntity
import com.rt.rtsl.utils.ExceptionHandler
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager
import io.reactivex.Observable
import okhttp3.Request
import org.json.JSONObject
import retrofit2.http.Body

interface ServerApi
{
    @POST("api/ap_user/sendSms")
    fun getSmsCode(@Body phoneNumber:SmsCodeEntity):Observable<JSONObject>

    @POST("/api/ap_user/login")
    fun login(@Body loginEntity: LoginEntity):Observable<JSONObject>
}

object AppApi
{
    var ReadTimeOut = 5L
    var ConnectTimeOut = 5L
    //服务器接口地址修改就改这
    val Host="http://112.74.96.214:9001"
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
            .baseUrl(Host)
            .addConverterFactory(JSONObjectConvertFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        serverApi=retrofit.create(ServerApi::class.java)
    }

}