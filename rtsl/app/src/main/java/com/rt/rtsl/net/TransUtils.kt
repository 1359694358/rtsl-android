package com.rt.rtsl.net;


import com.google.gson.Gson
import org.json.JSONObject

import io.reactivex.CompletableTransformer
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object TransUtils {
     val gson=Gson()
    @JvmStatic
    fun <T> jsonTransform(classRef: Class<T>): ObservableTransformer<JSONObject, T> {
        return ObservableTransformer { upstream ->
            upstream.map { s ->
                gson.fromJson("$s",classRef)
            }
        }
    }

    @JvmStatic
    fun  appLoginTokenExpireHandle(): ObservableTransformer<JSONObject, JSONObject> {
        return ObservableTransformer { upstream ->
            upstream.map { result ->
                result
            }
        }
    }

    fun <T> schedulersFlowableTransformer(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

    @JvmStatic
    fun <T> schedulersTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }


    fun schedulersCompletableTransformer(): CompletableTransformer {
        return CompletableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }
}
