package com.rt.rtsl.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.FrameLayout
import com.rt.rtsl.utils.FileUtil
import com.rtsl.app.android.R
import com.rtsl.app.android.databinding.ActivityOfficefileviewBinding
import com.tencent.smtt.sdk.TbsReaderView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity


class OfficeFileViewActivity: BaseActivity<ActivityOfficefileviewBinding>() {
    companion object
    {
        private fun getFileType(paramString: String): String {
            var str: String = ""
            if (TextUtils.isEmpty(paramString)) {
                return str
            }
            val i = paramString.lastIndexOf('.')
            if (i <= -1) {
                return str
            }
            str = paramString.substring(i + 1)
            return str
        }
        private const val FilePath="FilePath"
        private const val Title="Title"
        fun startActivity(context: Context,filePath:String,title:String="")
        {
            context.startActivity<OfficeFileViewActivity>(Pair(FilePath,filePath),Pair(Title,title))
        }
    }
    override fun getLayoutResId(): Int {
        return R.layout.activity_officefileview
    }
     var tbsReaderView:TbsReaderView?=null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        tbsReaderView=TbsReaderView(this,
            TbsReaderView.ReaderCallback { p0, p1, p2 ->

            })
        var title:String?=intent.getStringExtra(Title)
        title?.let {
            setTitle(it)
        }
        contentBinding.fileContainer.addView(tbsReaderView,FrameLayout.LayoutParams(-1,-1))
        contentBinding.fileContainer.allowLeftRightTouch=false
        val bundle = Bundle()
        var filePath:String?=intent.getStringExtra(FilePath)
        bundle.putString("filePath", filePath)
        bundle.putString("tempPath", FileUtil.CACHE)
        filePath?.let {
            val result: Boolean = tbsReaderView?.preOpen(getFileType(filePath), false)?:false
            if (result) {
                tbsReaderView?.openFile(bundle)
            } else {
            }
        }

    }

    override fun onDestroy() {
            tbsReaderView?.onStop()
        super.onDestroy()
    }
}