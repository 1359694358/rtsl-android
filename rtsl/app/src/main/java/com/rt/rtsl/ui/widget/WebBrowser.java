package com.rt.rtsl.ui.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.CallSuper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import com.rt.rtsl.utils.FileUtil;
import com.rtsl.app.android.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * 浏览器扩展 集成 了文件上传
 * 文件上传 用的ChoosePic里面的库来选择 如果没找到的话 那就用系统自带的选择器
 */
public class WebBrowser extends WebView {
    public static final int CAMERA_IMAGE = 3;
    public static final int CAMERA_VIDEO = 4;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    public static String chooseFilePrompt = "请选择文件";
    public static String TAG = WebBrowser.class.getSimpleName();
    public static String Msg_Title = "来自网页的消息 ";
    public static String OKAY = "确定";
    public static String CANCEL = "取消";
    public static String[] modes = new String[]{"相机", "相册"};
    public static String chooseTitle = "请选择上传方式";
    protected Context mContext;
    protected WebClient webClient;
    protected WebChrome webChrome;


    public ZoomTouchListener zoomTouchListener;

    public WebBrowser(Context context) {
        super(context);
        this.mContext = context;
        setBaseConfig();
    }

    public WebBrowser(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setBaseConfig();
    }

    public WebBrowser(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        setBaseConfig();
    }

    @Override
    public void destroy() {
        zoomTouchListener = null;
        mContext = null;
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        if(jsRefrence!=null&&!jsRefrence.isEmpty())
        {
            Iterator<String> iterator=jsRefrence.keySet().iterator();
            while (iterator!=null&&iterator.hasNext())
            {
                String  entry= iterator.next();
                removeJavascriptInterface(entry);
            }
            jsRefrence.clear();
        }
        jsRefrence=null;

        stopLoading();
        removeAllViews();
        clearWebClient();
        clearWebChrome();
        super.destroy();
    }


    protected void clearWebClient() {
        if (webClient != null) {
            webClient.context = null;
            webClient.webBrowser = null;
        }
        super.setWebViewClient(null);
    }

    protected void clearWebChrome() {
        if (webChrome != null)
            webChrome.destory();
        super.setWebChromeClient(null);
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public void setBaseConfig() {
        /*if (Build.VERSION.SDK_INT >= 19)
            setWebContentsDebuggingEnabled(true);
        getSettings().setDatabaseEnabled(true);*/
        setWebSetting(this);

        /*setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String dir = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        getSettings().setGeolocationDatabasePath(dir);
        getSettings().setGeolocationEnabled(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDefaultTextEncodingName("UTF-8");
        getSettings().setAppCacheEnabled(false);
        getSettings().setSavePassword(false);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 21) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
      //  addJavascriptInterface(new WebAppInterface(mContext), "JSInterface");
        getSettings().setPluginState(WebSettings.PluginState.ON);*/
        setWebViewClient(new WebClient(mContext, this));
        setWebChromeClient(new WebChrome(mContext, this));
        setDownloadListener(new WebViewDownLoadListener());
//        set17Api();

    }


    Map<String,Object>  jsRefrence=new HashMap<>();
    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object object, String name) {
        super.addJavascriptInterface(object, name);
        jsRefrence.put(name,object);
    }

    @Deprecated
    public void setWebChromeClient(WebChromeClient client) {
		super.setWebChromeClient(client);
//        throw new IllegalArgumentException("please use WebChrome instance ");
    }

    @Deprecated
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
//        throw new IllegalArgumentException("please use WebClient instance ");
    }

    public void setWebChromeClient(WebChrome client) {
        clearWebChrome();
        this.webChrome = client;
        super.setWebChromeClient(client);
    }

    public WebClient getWebBrowserClient()
    {
        return webClient;
    }

    public void setWebViewClient(WebClient client) {
        clearWebClient();
        this.webClient = client;
        super.setWebViewClient(client);
    }

   /* @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void set17Api() {
        if (Build.VERSION.SDK_INT >= 17) {
            getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
    }*/

    public static void setWebSetting(WebView webView) {
        if (Build.VERSION.SDK_INT >= 17) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= 19)
            webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String dir = webView.getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setGeolocationDatabasePath(dir);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true); // 允许访问文件
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
//        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        Log.w(TAG, "url:" + url);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
        Log.w(TAG, "url:" + url);
    }

    @Override
    public boolean canGoBack() {

        if (webChrome != null && webChrome.isFullScreen) {
            webChrome.onHideCustomView();
            return true;
        }
        return super.canGoBack();
    }

    /**
     * ���ñ��ص绰  ���󵯴�
     */
    public static class WebClient extends WebViewClient {
        public Context context;
        public WebView webBrowser;

        public WebClient(Context context, WebView webBrowser) {
            this.context = context;
            this.webBrowser = webBrowser;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            //调用拨号邮件等程序
//            if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:") || url.startsWith("sms:")) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                context.startActivity(intent);
//                return true;
//            }
            if (URLUtil.isNetworkUrl(url)) {
                if (url.endsWith("php")) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                view.loadUrl(url);
            } else {
                matchDefaultUrl(url);
            }
            return true;
        }

        public boolean matchDefaultUrl(String url) {
            final Intent intent = new Intent(Intent.ACTION_DEFAULT, Uri.parse(url));
            if (queryIntentActivities(context, intent))
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                String msg=context.getResources().getString(R.string.app_name)+"请求打开"+queryIntentActivityName(context,intent);
                builder.setTitle(Msg_Title);
                builder.setMessage(msg);
                builder.setCancelable(false);
                builder.setNegativeButton(WebBrowser.CANCEL, new AlertDialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(WebBrowser.OKAY, new AlertDialog.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(intent);
                    }
                });
                builder.show();
                return true;
            }
            return false;
        }

        public static boolean queryIntentActivities(Context context, Intent intent) {
            List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return resolveInfoList != null && resolveInfoList.size() > 0;
        }


        public static String  queryIntentActivityName(Context context, Intent intent) {
            List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if(resolveInfoList != null && resolveInfoList.size() > 0)
            {
                return resolveInfoList.get(0).loadLabel(context.getPackageManager()).toString();
            }
            return null;
        }


        public void loadUrl(WebView view, String url) {
            //			url=URLDecoder.decode(url);
            view.loadUrl(url);
            Log.w(TAG, "url:" + url);
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            pageError();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            pageError();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            pageError();
        }

        @CallSuper
        @Override
        public void onReceivedSslError(WebView view,
                                       SslErrorHandler handler, SslError error) {
            handler.proceed();
            pageError();
        }

        @CallSuper
        protected void pageError() {

        }
    }

    /**
     * js��ʾ��gps��λ �ļ�ѡ�񱾵�
     */
    public static class WebChrome extends WebChromeClient {
        protected Context context;
        protected WebView webBrowser;
        FrameLayout.LayoutParams fullScreenLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        public static final String MIME_VIDEO = "video/*";
        public static final String MIME_IMG = "image/*";
        public static final String PicActivity = "com.multi.choosepic.PicActivity";
        public static final String VideoActivity = "com.multi.choosevideo.VideoActivity";
        protected Fragment fragment;
        CameraTask cameraTask;

        public WebChrome(Context context, WebView webBrowser) {
            this.context = context;
            this.webBrowser = webBrowser;
        }

        public WebChrome(Context context, WebView webBrowser, Fragment fragment) {
            this(context, webBrowser);
            this.fragment = fragment;
        }

        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            Log.w(TAG, message + " -- From line " + lineNumber + " of " + sourceID);
        }

        @CallSuper
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.w(TAG, view.getOriginalUrl() + " progress:" + newProgress);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @CallSuper
        @Override
        public boolean onJsAlert(WebView view, String url,
                                 String message, final JsResult result) {
            try {
                result.confirm();
                Log.w(WebBrowser.TAG,message);
                /*AlertDialog.Builder b2 = new AlertDialog.Builder(context);
                b2.setTitle(Msg_Title).setMessage(message)
                        .setPositiveButton(OKAY,
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();*/
            } catch (Exception e) {
                // TODO: handle exception
            }

            return true;
        }

        @CallSuper
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setCancelable(false);
            b.setTitle(Msg_Title);
            b.setMessage(message);
            b.setPositiveButton(OKAY, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            b.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            b.create().show();
            return true;
        }

        @CallSuper
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setCancelable(false);
            b.setTitle(message);
            FrameLayout frameLayout = new FrameLayout(context);
            final EditText editText = new EditText(context);
            editText.setText(defaultValue);
            frameLayout.addView(editText);
            editText.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            b.setView(frameLayout);
            b.setPositiveButton(OKAY, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String value = editText.getText().toString();
                    result.confirm(value);
                }
            });
            b.setNegativeButton(CANCEL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            b.create().show();
            return true;
        }

        //扩展浏览器上传文件

        //3.0--版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg, null);
        }

        //3.0++版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg, acceptType);
        }


        //4.1>v<5
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg, acceptType);
        }

        // For Android > 5.0
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
            String[] acceptTypes = fileChooserParams.getAcceptTypes();
            String acceptType = acceptTypes.length > 0 ? acceptTypes[0] : null;
            openFileChooserImplForAndroid5(uploadMsg, acceptType);
            return true;
        }


        private void openFileChooserImpl(ValueCallback<Uri> uploadMsg, final String acceptType) {
            setUploadMessage(uploadMsg);
            if (isAnyFileChooseMode(acceptType, true))
                return;
            if (haveCamera()) {

                new AlertDialog.Builder(context)
                        .setTitle(chooseTitle)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (getUploadMessageForAndroid5() != null)
                                    getUploadMessageForAndroid5().onReceiveValue(new Uri[]{});
                                if (getUploadMessage() != null)
                                    getUploadMessage().onReceiveValue(null);
                                setUploadMessage(null);
                                setUploadMessage(null);
                            }
                        })
                        .setItems(modes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
                                        handleCameraChoose(acceptType, true);
                                    } else if (Build.VERSION.SDK_INT >= 23) {
                                        requestCameraPermissons(acceptType, true);
                                    } else
                                        handleCameraChoose(acceptType, true);
                                } else {
                                    handleNoCameraChoose(acceptType, true);
                                }
                            }
                        }).show();
            } else {
                handleNoCameraChoose(acceptType, true);
            }
        }

        void handleCameraChoose(String acceptType, boolean isBelowAndroid5) {
            setCustomChooseUIMode(false);
            Intent intent;
            if (MIME_IMG.equals(acceptType)) {
                try {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    setImgPath(FileUtil.PHOTO_APP +System.currentTimeMillis() + ".jpg");
                    File file = FileUtil.createFile(getImgPath());
                    Uri imageUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        imageUri= FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
                    }
                    else
                        imageUri= Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    ((Activity) context).startActivityForResult(intent, CAMERA_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(context, "没有相机权限", Toast.LENGTH_SHORT).show();
                }

            } else if (MIME_VIDEO.equals(acceptType)) {
                try {
                    intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    ((Activity) context).startActivityForResult(intent, CAMERA_VIDEO);
                } catch (Exception e) {
                    Toast.makeText(context,"没有相机权限", Toast.LENGTH_SHORT).show();
                }

            } else {
                createSystemNativeChooser(acceptType, isBelowAndroid5);
            }

        }

        void handleNoCameraChoose(String acceptType, boolean isBelowAndroid5) {
            if (MIME_IMG.equals(acceptType)) {
               /* if (PicActivityClassRef != null)
                    createCustomMediaChooser(acceptType, isBelowAndroid5);
                else*/
                    createSystemNativeChooser(acceptType, isBelowAndroid5);
            } else if (MIME_VIDEO.equals(acceptType)) {

              /*  if (VideoActivityClassRef != null)
                    createCustomMediaChooser(acceptType, isBelowAndroid5);
                else*/
                    createSystemNativeChooser(acceptType, isBelowAndroid5);
            } else {
                createSystemNativeChooser(acceptType, isBelowAndroid5);
            }
        }

        boolean haveCamera() {
            int cameraCount = Camera.getNumberOfCameras();
            return cameraCount > 0;
        }

        private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg, final String acceptType) {
            setUploadMessageForAndroid5(uploadMsg);
            if (isAnyFileChooseMode(acceptType, true))
                return;
            if (haveCamera()) {
                new AlertDialog.Builder(context)
                        .setTitle(chooseTitle)
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (getUploadMessageForAndroid5() != null)
                                    getUploadMessageForAndroid5().onReceiveValue(new Uri[]{});
                                if (getUploadMessage() != null)
                                    getUploadMessage().onReceiveValue(null);
                                setUploadMessage(null);
                                setUploadMessageForAndroid5(null);
                            }
                        })
                        .setItems(modes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
                                        handleCameraChoose(acceptType, false);
                                    } else if (Build.VERSION.SDK_INT >= 23) {
                                        requestCameraPermissons(acceptType, false);
                                    } else {
                                        handleCameraChoose(acceptType, false);
                                    }
                                } else {
                                    handleNoCameraChoose(acceptType, false);
                                }
                            }
                        }).show();
            } else {
                handleNoCameraChoose(acceptType, false);
            }
        }

       /* protected void createCustomMediaChooser(String acceptType, boolean isBelowAndroid5) {
            Intent intent = new Intent();
            try {
                int color = ((FragmentActivity4ChangeTheme) context).getMainColor();
                intent.putExtra("color", color);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (MIME_IMG.equals(acceptType))
                intent.setClass(context, PicActivityClassRef);
            else
                intent.setClass(context, VideoActivityClassRef);
            intent.putExtra("MutipleMode", false);
            setCustomChooseUIMode(true);
            int requestCode = isBelowAndroid5 ? FILECHOOSER_RESULTCODE : FILECHOOSER_RESULTCODE_FOR_ANDROID_5;
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                if (context instanceof Activity)
                    ((Activity) context).startActivityForResult(intent, requestCode);
                else if (context instanceof FragmentActivity)
                    ((FragmentActivity) context).startActivityForResult(intent, requestCode);
            }
        }*/

        /**
         * 任意选择模式
         *
         * @param acceptType
         * @param isBelowAndroid5
         * @return
         */
        boolean isAnyFileChooseMode(String acceptType, boolean isBelowAndroid5) {
            if (TextUtils.isEmpty(acceptType) || "*/*".equals(acceptType) || (!acceptType.toLowerCase(Locale.getDefault()).startsWith("video/") && !acceptType.toLowerCase(Locale.getDefault()).startsWith("image/"))) {
                createSystemNativeChooser(acceptType, isBelowAndroid5);
                return true;
            }
            return false;
        }

        /**
         * 创建系统原生的选择器
         *
         * @param acceptType
         * @param isBelowAndroid5 是否是5.0以下的系统
         */
        protected void createSystemNativeChooser(String acceptType, boolean isBelowAndroid5) {
            setCustomChooseUIMode(false);
            int requestCode = isBelowAndroid5 ? FILECHOOSER_RESULTCODE : FILECHOOSER_RESULTCODE_FOR_ANDROID_5;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            if (TextUtils.isEmpty(acceptType) || acceptType.indexOf("/") == -1) {
                acceptType = "*/*";
            }
            i.setType(acceptType);
            if (fragment != null) {
//                fragment.startActivityForResult(Intent.createChooser(i, "Choose File"), requestCode);
                fragment.startActivityForResult(i, requestCode);
            } else if (context instanceof Activity)
//                    ((Activity) context).startActivityForResult(Intent.createChooser(i, "Choose File"), requestCode);
                ((Activity) context).startActivityForResult(i, requestCode);

        }

        /**
         * --------------------start 处理全屏部分 @author zxd----------------------
         **/

        CustomViewCallback customViewCallback;

        View loadingView;

        @Override
        public final View getVideoLoadingProgressView() {
            if (loadingView == null)
                loadingView = new View(context);
            return loadingView;
        }

        View videoView;

        @Override
        public final void onShowCustomView(View view, CustomViewCallback callback) {
            if (isFullScreen)
                return;
            customViewCallback = callback;
            isFullScreen = true;
            FrameLayout decorView = (FrameLayout) ((Activity) context).getWindow().getDecorView();
            try {
                decorView.getChildAt(0).setVisibility(View.GONE);
                decorView.addView(view, fullScreenLp);
                ((Activity) context).getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                toggleFullScreen(true);
                view.setBackgroundColor(Color.BLACK);
                videoView = view;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(TAG, "webBrowser onShowCustomView .isFullScreen:" + isFullScreen);

        }

        boolean isFullScreen = false;

        final void toggleFullScreen(final boolean isFS) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isFullScreen = isFS;
                    if (!isFS) {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                        ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    } else {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        setFullSysUiV();
                    }

                    Log.w(TAG, "toggleFullScreen:" + isFullScreen);
                }
            });
        }

        final void setFullSysUiV() {
            int flag = /*View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |*/ View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= 19)
                flag |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(flag);
        }

        @Override
        public final void onHideCustomView() {
            if (!isFullScreen)
                return;
            FrameLayout decorView = (FrameLayout) ((Activity) context).getWindow().getDecorView();
            decorView.setBackgroundColor(Color.TRANSPARENT);
            try {
                decorView.getChildAt(0).setVisibility(View.VISIBLE);
                decorView.removeView(videoView);
                toggleFullScreen(false);
                if (customViewCallback != null) {
                    customViewCallback.onCustomViewHidden();
                }
                customViewCallback = null;
                videoView = null;
                webBrowser.requestFocus();
                ((Activity) context).getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (webBrowser != null)
                            ((Activity) context).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                }, 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(TAG, "webBrowser onHideCustomView.isFullScreen:" + isFullScreen);

        }


        /**
         * --------------------end 处理全屏部分 @author zxd----------------------
         **/

        public boolean isCustomChooseUIMode = false;

        public boolean getIsCustomChooseUIMode() {
            return isCustomChooseUIMode;
        }

        public void setCustomChooseUIMode(boolean value) {
            isCustomChooseUIMode = value;
        }

        public ValueCallback<Uri> mUploadMessage;
        public ValueCallback<Uri[]> mUploadMessageForAndroid5;

        public ValueCallback<Uri> getUploadMessage() {
            return mUploadMessage;
        }

        public void setUploadMessage(ValueCallback<Uri> valueCallback) {
            mUploadMessage = valueCallback;
        }

        public ValueCallback<Uri[]> getUploadMessageForAndroid5() {
            return mUploadMessageForAndroid5;
        }

        public void setUploadMessageForAndroid5(ValueCallback<Uri[]> valueCallback) {
            mUploadMessageForAndroid5 = valueCallback;
        }


        public final void rejectCameraPermissons() {
            if (mUploadMessageForAndroid5 != null)
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            if (mUploadMessage != null)
                mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
            mUploadMessageForAndroid5 = null;
            cameraTask = null;
        }

        /********
         * 权限处理 相机
         ********/

        public final void alllowedCameraPermissons() {
            if (cameraTask != null)
                handleCameraChoose(cameraTask.acceptType, cameraTask.isBelowAndroid5);
            else
                rejectCameraPermissons();
        }


        protected void reciveUri(Uri uri) {
            if (mUploadMessage != null)
                mUploadMessage.onReceiveValue(uri);
            else if (mUploadMessageForAndroid5 != null) {
                if (uri != null)
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{uri});
                else
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (isCustomChooseUIMode && intent != null) {
                    ArrayList<String> fileList = intent.getStringArrayListExtra("addrs");
                    if (fileList != null && fileList.size() > 0) {
                        String filePath = fileList.get(0);
                        Uri uri = Uri.fromFile(new File(filePath));
                        reciveUri(uri);
                    }
                } else {
                    Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                    reciveUri(result);
                }

            } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
                if (isCustomChooseUIMode && intent != null) {
                    ArrayList<String> fileList = intent.getStringArrayListExtra("addrs");
                    if (fileList != null && fileList.size() > 0) {
                        String filePath = fileList.get(0);
                        Uri uri = Uri.fromFile(new File(filePath));
                        reciveUri(uri);
                    } else {
                        reciveUri(null);
                    }
                } else {
                    Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
                    reciveUri(result);
                }
            } else if (requestCode == CAMERA_IMAGE) {
                Uri uri = null;
                try {
                    File file = new File(getImgPath());
                   /* Uri mUri = Uri.fromFile(file);*/
                    Uri mUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {
                        mUri=FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
                    }
                    else
                        mUri= Uri.fromFile(file);
                    uri = compressImage(context, mUri);
                } catch (Exception e) {
                    Log.e("", "=mye==" + e.getMessage());
                    e.printStackTrace();
                }
//            }
                reciveUri(uri);
                setImgPath(null);
            } else if (requestCode == CAMERA_VIDEO) {
                Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
                reciveUri(result);
            }
            mUploadMessage = null;
            mUploadMessageForAndroid5 = null;
            isCustomChooseUIMode = false;
        }

        public void requestCameraPermissons(String acceptType, boolean value) {
            cameraTask = new CameraTask();
            cameraTask.acceptType = acceptType;
            cameraTask.isBelowAndroid5 = value;
            String[] permssions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions((Activity) context, permssions, permssions.length);
        }

        String imgPath;

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String value) {
            imgPath = value;
        }

        public void destory() {
            customViewCallback = null;
            mUploadMessage = null;
            mUploadMessageForAndroid5 = null;
            context = null;
            webBrowser = null;
            cameraTask = null;
        }
    }


    /**
     * ��Activity onActivityResult�����лص�Щ����
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (webChrome != null)
            webChrome.onActivityResult(requestCode, resultCode, intent);
        if(jsRefrence!=null&&!jsRefrence.isEmpty())
        {
            Iterator<Map.Entry<String, Object>> iterator=jsRefrence.entrySet().iterator();
            while (iterator.hasNext())
            {
                Map.Entry<String, Object> entry=iterator.next();
                if(entry.getValue()!=null && entry.getValue() instanceof IActivityOnResult4Js)
                {
                    ((IActivityOnResult4Js)entry.getValue()).onActivityResult(requestCode,resultCode,intent);
                }
            }
        }
        /*if (requestCode == FILECHOOSER_RESULTCODE) {
            if (isCustomChooseUIMode && intent != null) {
                ArrayList<String> fileList = intent.getStringArrayListExtra("addrs");
                if (fileList != null && fileList.size() > 0)
                {
                    String filePath = fileList.get(0);
                    Uri uri = Uri.fromFile(new File(filePath));
                    reciveUri(uri);
                }
            } else {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                reciveUri(result);
            }

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5)
        {
            if (isCustomChooseUIMode && intent != null) {
                ArrayList<String> fileList = intent.getStringArrayListExtra("addrs");
                if (fileList != null && fileList.size() > 0) {
                    String filePath = fileList.get(0);
                    Uri uri = Uri.fromFile(new File(filePath));
                    reciveUri(uri);
                } else {
                    reciveUri(null);
                }
            } else {
                Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
                reciveUri(result);
            }
        } else if (requestCode == CAMERA_IMAGE) {
            Uri uri = null;
            try {
                File file = new File(imgPath);
                Uri mUri = Uri.fromFile(file);
                uri = compressImage(mContext, mUri);
            } catch (Exception e) {
                Log.e("", "=mye==" + e.getMessage());
                e.printStackTrace();
            }
//            }
            reciveUri(uri);
            imgPath = null;
        } else if (requestCode == CAMERA_VIDEO) {
            Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
            reciveUri(result);
        }
        mUploadMessage = null;
        mUploadMessageForAndroid5 = null;
        isCustomChooseUIMode = false;*/
    }


    /**
     * ����ϵͳĬ�ϵ�����
     */
    public class WebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Log.i(TAG, "url=" + url);
            Log.i(TAG, "userAgent=" + userAgent);
            Log.i(TAG, "contentDisposition=" + contentDisposition);
            Log.i(TAG, "mimetype=" + mimetype);
            Log.i(TAG, "contentLength=" + contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
    }


    boolean isDoubleTouch = false;
    float beforeLenght, afterLenght = 0.0f;
    boolean isScaleBig = false;
    boolean isScaleSmall = false;

    float downx, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
//			Toast.makeText(getContext(),"2个点",Toast.LENGTH_SHORT).show();
            isDoubleTouch = true;
            float moveX = event.getX(1) - event.getX(0);
            float moveY = event.getY(1) - event.getY(0);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isScaleBig = isScaleSmall = false;
                    beforeLenght = (float) Math.sqrt((moveX * moveX) + (moveY * moveY));
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 得到两个点之间的长度
                    afterLenght = (float) Math.sqrt((moveX * moveX) + (moveY * moveY));

                    float gapLenght = afterLenght - beforeLenght;

                    if (gapLenght == 0) {
                        break;
                    }

                    // 如果当前时间两点距离大于前一时间两点距离，则传0，否则传1
                    if (gapLenght > 0) {
                        isScaleBig = true;
                        isScaleSmall = false;
//						Toast.makeText(getContext(),"放大",Toast.LENGTH_SHORT).show();
                    } else {
                        isScaleBig = false;
                        isScaleSmall = true;
//						Toast.makeText(getContext(),"缩小",Toast.LENGTH_SHORT).show();
                    }

                    beforeLenght = afterLenght;
                    break;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downx = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getX() == downx && event.getY() == downY) {
                        if (webChrome != null && webChrome.isFullScreen) {
                            webChrome.setFullSysUiV();
                        }
                    }
                    break;
            }

        }
        if (isDoubleTouch && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            if (isScaleBig) {
                if (zoomTouchListener != null)
                    zoomTouchListener.zoomLarge();
            } else if (isScaleSmall) {
                if (zoomTouchListener != null)
                    zoomTouchListener.zoomSmall();
            }
            isDoubleTouch = false;
        }
        return super.onTouchEvent(event);
    }

    public interface ZoomTouchListener {
        void zoomLarge();

        void zoomSmall();
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param imageUri
     */
    public static Uri compressImage(Context context, Uri imageUri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, output);
        byte[] bytes = output.toByteArray();
        bitmap.recycle();
        output.close();
        String fileName = "img_" + System.currentTimeMillis() + ".jpg";
        File file = FileUtil.saveData(bytes, fileName);
//        Uri mUri = Uri.fromFile(file);
        Uri mUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            mUri=FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
        }
        else
            mUri= Uri.fromFile(file);
        return mUri;
    }


    /********
     * 权限处理 相机
     ********/

    public final void alllowedCameraPermissons() {
        if (webChrome != null && webChrome.cameraTask != null)
            webChrome.alllowedCameraPermissons();
        else
            webChrome.rejectCameraPermissons();
    }


    public final void rejectCameraPermissons() {
        /*if (mUploadMessageForAndroid5 != null)
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
        if (mUploadMessage != null)
            mUploadMessage.onReceiveValue(null);
        mUploadMessage = null;
        mUploadMessageForAndroid5 = null;*/

        if (webChrome != null)
            webChrome.rejectCameraPermissons();
    }

    /********
     * 权限处理 相机
     ********/

    public static class CameraTask {
        public String acceptType;
        public boolean isBelowAndroid5;
    }
/*
    ActionMode.Callback mCallback = new ActionMode.Callback() {
        //Called when action mode is first created.
        // The menu supplied will be used to generate action buttons for the action mode
        //当ActionMode第一次被创建的时候调用
        //可在这里初始化menu
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.copy_mene, menu);
            menu.findItem(R.id.web_copy).setTitle(mContext.getString(R.string.web_copy));
            return true;//返回true表示action mode会被创建  false if entering this mode should be aborted.
        }

        //
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        //当ActionMode被点击时调用
        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.web_copy) {
                getSelectedData();
                mode.finish();
                clearFocus();
                return true;
            }
            return false;
        }

        private void getSelectedData() {

            String js = "(function getSelectedText() {" +
                    "var txt;" +
                    "if (window.getSelection) {" +
                    "txt = window.getSelection().toString();" +
                    "} else if (window.document.getSelection) {" +
                    "txt = window.document.getSelection().toString();" +
                    "} else if (window.document.selection) {" +
                    "txt = window.document.selection.createRange().text;" +
                    "}" +
                    "JSInterface.getText(txt);" +
                    "})()";
            // calling the js function
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                evaluateJavascript("javascript:" + js, null);
            } else {
                loadUrl("javascript:" + js);
            }
        }


        //Called when an action mode is about to be exited and destroyed.
        //当ActionMode退出销毁时调用
        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };


    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        return super.startActionMode(mCallback);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return super.startActionMode(mCallback, type);
    }

    @Override
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
        return super.startActionModeForChild(originalView, callback);
    }

    @Override
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback, int type) {
        return super.startActionModeForChild(originalView, callback, type);
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void getText(String text) {
            // put selected text into clipdata
            ClipboardManager clipboard = (ClipboardManager)
                    mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text", text);
            clipboard.setPrimaryClip(clip);
            // gives the toast for selected text
            Toast.makeText(mContext, mContext.getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
        }
    }*/

    public static class WebViewCameraChooseProvider extends FileProvider
    {

    }

    public interface IActivityOnResult4Js
    {
        void onActivityResult(int requestCode, int resultCode, Intent intent);
    }
}