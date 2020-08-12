package com.rt.rtsl.net

import com.rt.rtsl.utils.ExceptionHandler
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.X509TrustManager

interface ServerApi
{

}

object AppApi
{
    var ReadTimeOut = 5L
    var ConnectTimeOut = 5L
    val Host="https://"
    val serverApi:ServerApi
    init {
        RxJavaPlugins.setErrorHandler { throwable ->
            ExceptionHandler.getInstance().saveCrashInfo2File(throwable)
        }
        val okhttpBuilder = OkHttpClient.Builder()
        okhttpBuilder.readTimeout(ReadTimeOut, TimeUnit.SECONDS)
        okhttpBuilder.connectTimeout(ConnectTimeOut, TimeUnit.SECONDS)
        if (Host.startsWith("https",true))
        {
            okhttpBuilder.sslSocketFactory(
                    SSLSocketClient.getSSLSocketFactory(),
                    SSLSocketClient.getTrustManager()[0] as X509TrustManager
            )
            okhttpBuilder.hostnameVerifier(SSLSocketClient.getHostnameVerifier())
        }
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