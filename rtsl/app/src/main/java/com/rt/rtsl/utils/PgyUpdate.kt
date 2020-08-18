package com.rt.rtsl.utils

import android.app.Activity
import android.util.Log
import com.pgyersdk.update.DownloadFileListener
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import com.pgyersdk.update.javabean.AppBean
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import java.io.File
import java.lang.Exception

object PgyUpdate
{
    fun updateCheck(activity:Activity)
    {
        var pgyUpdateBuilder= PgyUpdateManager.Builder()
        pgyUpdateBuilder.setUpdateManagerListener(object: UpdateManagerListener
        {
            override fun onUpdateAvailable(appBean: AppBean?)
            {
                Log.d("Pgy", "onUpdateAvailable: $appBean")
                appBean?.let {
                    PgyUpdateManager.downLoadApk(appBean.downloadURL);
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
                        dialog.dismiss()
                        PgyUpdateManager.installApk(file)
                    }
                    dialogBuilder.addAction("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialogBuilder.show()
                };
            }

        })
        pgyUpdateBuilder.register()
    }
}