package com.rt.rtsl.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


/**
 * Created by oliver on 15/7/14.
 */
enum class WxShareUtils {
    INSTANCE;

    private var mWXApi: IWXAPI? = null

    val THUMB_IMAGE_REQUEST_WIDTH = 80
    val THUMB_IMAGE_REQUEST_HEIGHT = 80

    private val mKey = "wx5a3a992ae23c31de"
    fun init(context: Context, appId: String) {
        retrieveWXAPI(context, appId)
    }
    /**
     * 分享单图到微信好友或者微信朋友圈，有缩略图
     * @param context
     * @param scene         session and moment
     * @param bitmap
     * @param mThumbBmp     缩略图
     */
    /**
     * 分享单图到微信好友或者微信朋友圈，无缩略图
     * @param context
     * @param scene         session and moment
     * @param bitmap
     */
    @JvmOverloads
    fun shareSinglePic(
        context: Context,
        scene: Int,
        bitmap: Bitmap,
        mThumbBmp: Bitmap? = null
    ) {
        if (mWXApi == null) {
            retrieveWXAPI(context, mKey)
        }
        val imgObj = WXImageObject()
        imgObj.imageData = bmpToByteArray(bitmap)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        val thumbBmp: Bitmap
        thumbBmp = if (mThumbBmp != null) {
            Bitmap.createScaledBitmap(
                mThumbBmp,
                THUMB_IMAGE_REQUEST_WIDTH,
                THUMB_IMAGE_REQUEST_HEIGHT,
                true
            )
        } else {
            Bitmap.createScaledBitmap(
                bitmap,
                THUMB_IMAGE_REQUEST_WIDTH,
                THUMB_IMAGE_REQUEST_HEIGHT,
                true
            )
        }
        msg.thumbData = bmpToByteArray(thumbBmp)
        bitmap.recycle()
        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = buildTransaction("img")
        req.message = msg
        req.scene = scene
        mWXApi?.sendReq(req)
    }

    /**
     * share text to wechat session and moment
     *
     * @param scene      session or timeline
     * @param desc
     */
    fun shareText(context: Context, scene: Int, desc: String?) {
        if (mWXApi == null) {
            retrieveWXAPI(context, mKey)
        }
        val textObject = WXTextObject()
        textObject.text = desc
        val msg = WXMediaMessage()
        msg.mediaObject = textObject
        msg.description = desc
        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = buildTransaction("text")
        req.message = msg
        req.scene = scene
        mWXApi?.sendReq(req)
    }

    /**
     * share web page to wechat session and moment
     *
     * @param scene      session or timeline
     * @param webPageUrl
     * @param title
     * @param desc
     * @param bitmap
     */
    fun shareWebPage(
        context: Context, scene: Int, webPageUrl: String?,
        title: String?, desc: String?, bitmap: Bitmap?
    ) {
        if (mWXApi == null) {
            retrieveWXAPI(context, mKey)
        }
        val webpage = WXWebpageObject()
        webpage.webpageUrl = webPageUrl
        val msg = WXMediaMessage(webpage)
        msg.title = title
        msg.description = desc
        if(bitmap != null){
            val thumbBmp: Bitmap
            thumbBmp =
                Bitmap.createScaledBitmap(
                    bitmap,
                    THUMB_IMAGE_REQUEST_WIDTH,
                    THUMB_IMAGE_REQUEST_HEIGHT,
                    true
                )
            msg.thumbData = bmpToByteArray(thumbBmp)
        }else{
            msg.thumbData = bmpToByteArray(bitmap)
        }

        val req: SendMessageToWX.Req = SendMessageToWX.Req()
        req.transaction = buildTransaction("webpage")
        req.message = msg
        req.scene = scene
        mWXApi?.sendReq(req)
    }

    /**
     * 微信好友或微信朋友圈单图分享，绕过微信SDK
     *
     * @param context
     * @param path
     * @param desc
     */
    fun shareSinglePicWithoutSDK(
        context: Context,
        scene: Int,
        path: String?,
        desc: String?
    ) {
        disableStrictMode()
        val imageUri = Uri.fromFile(File(path))
        val intent = Intent()
        val comp: ComponentName
        comp = if (scene == SendMessageToWX.Req.WXSceneSession) {
            ComponentName(
                "com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI"
            )
        } else {
            ComponentName(
                "com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI"
            )
        }
        intent.component = comp
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.putExtra("Kdescription", desc)
        context.startActivity(intent)
    }

    /**
     * 微信好友和朋友圈多图分享，绕过SDK
     *
     * @param localPicsList 图片文件路径列表
     * @param desc          文字描述
     */
    fun shareMultiplePicsWithoutSDK(
        context: Context,
        scene: Int,
        localPicsList: ArrayList<String>,
        desc: String?
    ) {
        disableStrictMode()
        val intent = Intent()
        val comp: ComponentName
        comp = if (scene == SendMessageToWX.Req.WXSceneSession) {
            ComponentName(
                "com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI"
            )
        } else {
            ComponentName(
                "com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI"
            )
        }
        intent.component = comp
        intent.action = "android.intent.action.SEND_MULTIPLE"
        val localArrayList =
            ArrayList<Uri>()
        val len = localPicsList.size
        for (i in 0 until len) {
            localArrayList.add(Uri.parse("file:///" + localPicsList[i]))
        }
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList)
        intent.type = "image/*"
        intent.putExtra("Kdescription", desc)
        context.startActivity(intent)
    }

    private fun disableStrictMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val ddfu =
                    StrictMode::class.java.getDeclaredMethod("disableDeathOnFileUriExposure")
                ddfu.invoke(null)
            } catch (e: Exception) {
            }
        }
    }

    private fun retrieveWXAPI(context: Context, appId: String) {
        mWXApi = WXAPIFactory.createWXAPI(context, appId)
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis()
            .toString() else type + System
            .currentTimeMillis()
    }

    companion object {
        private const val THUMB_SIZE = 100
    }

    open fun bmpToByteArray(bitmap: Bitmap?): ByteArray? {
        val stream = ByteArrayOutputStream()
        if (bitmap == null) {
            return ByteArray(0)
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}