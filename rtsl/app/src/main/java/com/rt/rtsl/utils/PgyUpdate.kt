package com.rt.rtsl.utils

import android.app.Activity
import android.util.Log
import com.pgyersdk.update.DownloadFileListener
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.pgyersdk.update.javabean.AppBean
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import com.rt.rtsl.bean.result.UpdateAppResultBean
import com.rt.rtsl.net.AppApi
import com.rt.rtsl.net.TransUtils
import java.io.File
import java.lang.Exception

object PgyUpdate
{
    private var isForced="0";
    fun updateCheck(activity:Activity)
    {
        var versionName=activity.packageManager.getPackageInfo(activity.packageName,0).versionName.replace(".","").toInt()
        logd("versionName:$versionName")
        var pgyUpdateBuilder= PgyUpdateManager.Builder()
        var updateListener:(autoDownload:Boolean)->Unit=
            {autoDownload->
                pgyUpdateBuilder.setUpdateManagerListener(object: UpdateManagerListener
                {
                    override fun onUpdateAvailable(appBean: AppBean?)
                    {
                        Log.d("Pgy", "onUpdateAvailable: $appBean")
                        if(autoDownload)
                        {
                            appBean?.let {
                                logd("用蒲公英的下载地址${appBean.downloadURL}")
                                PgyUpdateManager.downLoadApk(appBean.downloadURL);
                            }
                        }
                    }

                    override fun checkUpdateFailed(e: Exception?)
                    {
                        Log.d("Pgy", "checkUpdateFailed:${e} ")
                    }

                    override fun onNoUpdateAvailable()
                    {
                        Log.d("Pgy", "onNoUpdateAvailable")
                    }

                })
            }
        var downloadListener:()->Unit={
            pgyUpdateBuilder.setDownloadFileListener(object : DownloadFileListener
            {
                override fun downloadFailed() {
                }

                override fun onProgressUpdate(vararg args: Int?)
                {

                }

                override fun downloadSuccessful(file: File?)
                {
                    if(file!=null)
                    {
                        var dialogBuilder= QMUIDialog.MessageDialogBuilder(activity);
                        dialogBuilder.setTitle("更新提示")
                        dialogBuilder.setMessage("系统检测到新的版本，点击确认进行安装")
                        dialogBuilder.addAction("确定") { dialog, _ ->
                            if("1"!=isForced)
                                dialog.dismiss()
                            PgyUpdateManager.installApk(file)
                        }
                        if("1"!=isForced)
                        {
                            dialogBuilder.addAction("取消") { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        else
                        {
                            dialogBuilder.setCanceledOnTouchOutside(false)
                            dialogBuilder.setCancelable(false)
                        }
                        dialogBuilder.show()
                    };
                }

            })
        }

        AppApi.serverApi.checkUpdate()
            .compose(TransUtils.schedulersTransformer())
            .compose(TransUtils.jsonTransform(UpdateAppResultBean::class.java))
            .subscribe(
                {
                    if(it.yes() && versionName< it.data?.version?.replace(".","")?.toInt()?:0&&it.data?.url?.isNotEmpty()==true)
                    {
                        logd("用自己服务器的下载地址${it.data?.url}")
                        isForced=it.data.isForced
                        updateListener(false)
                        downloadListener()
                        pgyUpdateBuilder.register()
                        PgyUpdateManager.downLoadApk(it.data.url)
                    }
                    else
                    {
                        updateListener(true)
                        downloadListener()
                        pgyUpdateBuilder.register()
                    }
                }
                ,
                {
                    logd("$it")
                    updateListener(true)
                    downloadListener()
                    pgyUpdateBuilder.register()
                }
            )


    }
}