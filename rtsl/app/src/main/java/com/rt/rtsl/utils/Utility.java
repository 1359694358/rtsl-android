package com.rt.rtsl.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;
import androidx.core.graphics.drawable.DrawableCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Utility {
    public static final String LAYTOUT = "layout";
    public static final String DRAWABLE = "drawable";
    public static final String MIPMAP = "mipmap";
    public static final String MENU = "menu";
    public static final String RAW = "raw";
    public static final String ANIM = "anim";
    public static final String STRING = "string";
    public static final String STYLE = "style";
    public static final String STYLEABLE = "styleable";
    public static final String INTEGER = "integer";
    public static final String ID = "id";
    public static final String DIMEN = "dimen";
    public static final String COLOR = "color";
    public static final String FONT = "font";
    public static final String BOOL = "bool";
    public static final String ATTR = "attr";


    public static void showMessage(Context ctx, String title, String msg, String positive, String negative, OnClickListener sureListener, boolean canceEnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(canceEnable);
        builder.setNegativeButton(negative, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(positive, sureListener);
    }

    public static void showMessage(Context ctx, String title, String msg, String positive, String negative, OnClickListener sureListener, OnClickListener noListener, boolean canceEnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(canceEnable);
        builder.setNegativeButton(negative, noListener);
        builder.setPositiveButton(positive, sureListener);
    }


    public static void findAllSuperClassName(List<String> superClassNames, Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        superClassNames.add(clazz.getSimpleName());
        if (superClass != null) {
            superClassNames.add(superClass.getSimpleName());
            findAllSuperClassName(superClassNames, superClass);
        }
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int ResId) {
        Toast.makeText(context, ResId, Toast.LENGTH_SHORT).show();
    }

    public static boolean isPhoneNo(String phoneNumber) {
        String expression = "((^(13|14|15|16|17|18|19)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^(((13|14|15|16|17|18|19)[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isPersonID(String pid) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        String regX = "[0-9]{17}X";
        boolean flag = pid.matches(regx) || pid.matches(reg1) || pid.matches(regex) || pid.matches(regX);
        return flag;
    }

    public static boolean isEmail(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        boolean flag = email.matches(regex);
        return flag;
    }

    public static byte[] readResource(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] array = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(array)) != -1) {
            outputStream.write(array, 0, len);
        }
        inputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }

    private static final String PREFS_NAME = "DEFAULT";

    public static String[] readPreference(Context context, String[] keys) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String[] results = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            results[i] = settings.getString(keys[i], "");
        }
        return results;
    }

    public static String[] readPreference(Context context, String pref, String[] keys) {
        SharedPreferences settings = context.getSharedPreferences(pref, 0);
        String[] results = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            results[i] = settings.getString(keys[i], "");
        }
        return results;
    }

    public static void savePreference(Context context, String[] keys, String[] values) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        for (int i = 0; i < keys.length; i++) {
            editor.putString(keys[i], values[i]);
        }
        editor.commit();
    }

    public static void savePreference(Context context, String pref, String[] keys, String[] values) {
        SharedPreferences settings = context.getSharedPreferences(pref, 0);
        SharedPreferences.Editor editor = settings.edit();
        for (int i = 0; i < keys.length; i++) {
            editor.putString(keys[i], values[i]);
        }
        editor.commit();
    }

    public static int getNumber(TextView v, int def) {
        try {
            return Integer.parseInt(v.getText().toString());
        } catch (Exception e) {
            v.setText(String.valueOf(def));
            return def;
        }
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            verCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verCode;
    }

    /**
     * 放大/缩小bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        Bitmap newbmp = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidht, scaleHeight);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);
        }
        return newbmp;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2DP(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isAdMachine(Context context) {
        return (context.getResources().getDisplayMetrics().widthPixels == 720 || context.getResources().getDisplayMetrics().widthPixels == 1080) && context.getResources().getDisplayMetrics().densityDpi == 160;
    }

    /**
     * 取某个资源文件的id
     *
     * @param context
     * @param fileName 文件名
     * @param fileType 文件类型
     * @return
     */
    public static int getResId(Context context, String fileName, String fileType) {
        AssetManager assetManager = context.getResources().getAssets();
        try {
            Method getResourceIdentifier = assetManager.getClass().getDeclaredMethod("getResourceIdentifier", String.class, String.class, String.class);
            getResourceIdentifier.setAccessible(true);
            return (Integer) getResourceIdentifier.invoke(assetManager, fileName, fileType, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getResources().getIdentifier(fileName, fileType, context.getPackageName());
    }

    /**
     * 获取文本行高
     *
     * @param pTextView
     * @return
     */
    public static int getTextViewHeight(TextView pTextView) {
        Layout layout = pTextView.getLayout();
        if (layout == null)
            return 0;
        int desired = layout.getLineTop(pTextView.getLineCount());
        int padding = pTextView.getCompoundPaddingTop() + pTextView.getCompoundPaddingBottom();
        return desired + padding;
    }

    public static int getTextViewWidth(TextView pTextView) {
        Layout layout = pTextView.getLayout();
        if (layout == null)
            return 0;
        float desired = layout.getLineWidth(0);
        int padding = pTextView.getCompoundPaddingLeft() + pTextView.getCompoundPaddingRight();
        return (int) (desired + padding);
    }

    public static void setImageTintColor(int color, ImageView... imageViews) {
        for (ImageView imageView : imageViews) {
            if (imageView != null)
                imageView.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public static void setBitmapDrawableTintColor(int color, Drawable... drawables) {
        setBitmapDrawableTintColor(ColorStateList.valueOf(color), drawables);
    }

    public static void setBitmapDrawableTintColor(ColorStateList colorStateList, Drawable... drawables) {
        for (Drawable item : drawables) {
            if (item != null) {
                DrawableCompat.setTintList(item, colorStateList);
            }
        }
    }

    public static <T extends Drawable> T tintDrawable(int color, Drawable drawable) {
        Drawable newDrawable = DrawableCompat.wrap(drawable);
        setBitmapDrawableTintColor(color, newDrawable);
        newDrawable = newDrawable.mutate();
        newDrawable.invalidateSelf();
        return (T) newDrawable;
    }

    public static <T extends Drawable> T tintDrawable(ColorStateList colorStateList, Drawable drawable) {
        Drawable newDrawable = DrawableCompat.wrap(drawable);
        setBitmapDrawableTintColor(colorStateList, newDrawable);
        newDrawable = newDrawable.mutate();
        newDrawable.invalidateSelf();
        return (T) newDrawable;
    }

    public static void setViewBackGroundColor(int color, View... views) {
        for (View item : views) {
            if (item != null)
                item.setBackgroundColor(color);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View view, int id) {
        if (view == null)
            return null;
        return (T) view.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Window view, int id) {
        if (view == null)
            return null;
        return (T) view.findViewById(id);
    }

    public static void copyText(String text, Context context) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData myClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, text);
        myClipboard.setPrimaryClip(myClip);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(Serializable object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean netstatusOk(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        return !(activeInfo == null || !activeInfo.isAvailable());
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem == null)
                continue;
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /***
     * 打开app详情设置
     * @param context
     */
    public static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }


    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static void jumpNotificationSetting(Context context) {
        /*Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        context.startActivity(intent);*/

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, uid);
                //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                intent.putExtra("app_package", pkg);
                intent.putExtra("app_uid", uid);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
            } else {
                intent = new Intent(Settings.ACTION_SETTINGS);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }

    }

    /*public static File getGlideCacheFilePath(Context context, String url) {
        Glide glide = Glide.get(context);
        try {
            Field field = glide.getClass().getDeclaredField("engine");
            field.setAccessible(true);
            Engine engine = (Engine) field.get(glide);

            field = engine.getClass().getDeclaredField("diskCacheProvider");
            field.setAccessible(true);
            Object diskCacheProvider = field.get(engine);

            Method getDiskCache = diskCacheProvider.getClass().getDeclaredMethod("getDiskCache");
            getDiskCache.setAccessible(true);
            getDiskCache.invoke(diskCacheProvider);

            field = diskCacheProvider.getClass().getDeclaredField("diskCache");
            field.setAccessible(true);
            DiskCache disCache = (DiskCache) field.get(diskCacheProvider);
            if (TextUtils.isEmpty(url)) {
                return null;
            }
            File file = disCache.get(new GlideUrl(url));
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }*/

    public static boolean haveCamera(Context context) {
//		boolean haveback= pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
//		boolean havefront=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        int cameraCount = Camera.getNumberOfCameras();
        return cameraCount > 0;
    }

    public static Bitmap tintNewBitmap(Bitmap source, int tintColor) {
        Bitmap bitmap = source.copy(Bitmap.Config.ARGB_4444, true);
        int witdh = source.getWidth();
        int height = source.getHeight();
        for (int x = 0; x < witdh; x++) {
            for (int y = 0; y < height; y++) {
                int color = bitmap.getPixel(x, y);
                if (color != Color.TRANSPARENT) {
                    bitmap.setPixel(x, y, tintColor);
                }
            }
        }
        return bitmap;
    }

    public static Object invokeStaticMethod(Class object, String methodName, Object[] params, Class[] paramTypes) {
        Object returnObj = null;
        if (object == null) {
            return null;
        }
        Class cls = object;
        Method method = null;
        try {
            method = cls.getMethod(methodName, paramTypes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (method == null) {
            try {
                method = cls.getDeclaredMethod(methodName, paramTypes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (method != null) {
            method.setAccessible(true);
            try {
                returnObj = method.invoke(null, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnObj;
    }

    public static Object invokeMethod(Object object, String methodName, Object[] params, Class[] paramTypes) {
        Object returnObj = null;
        if (object == null) {
            return null;
        }
        Class cls = object.getClass();
        Method method = null;
        for (; cls != Object.class; cls = cls.getSuperclass()) {
            try {
                method = cls.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (method != null) {
            method.setAccessible(true);
            try {
                returnObj = method.invoke(object, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnObj;
    }


    public static <T> T invokeFiled(Object object, String fieldName) {
        Object returnObj = null;
        if (object == null || TextUtils.isEmpty(fieldName)) {
            return null;
        }
        Class cls = object.getClass();
        Field field = null;
        for (; cls != Object.class; cls = cls.getSuperclass()) {
            try {
                field = cls.getDeclaredField(fieldName);
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (field != null) {
            field.setAccessible(true);
            try {
                returnObj = field.get(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) returnObj;
    }

    public static String replaceUrl(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        return url.replaceAll("//", "/").replaceAll("http:/", "http://").replaceAll("https:/", "https://");
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static final boolean isOPenGPS(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    public static void openGPSSetting(Activity activity,int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, requestCode); // 设置完成后返回到原来的界面
    }

    public static String getRandomIp() {
        //ip的范围
        int[][] range_ip = {{607649792, 608174079},//36.56.0.0-36.63.255.255
                {1038614528, 1039007743},//61.232.0.0-61.237.255.255
                {1783627776, 1784676351},//106.80.0.0-106.95.255.255
                {2035023872, 2035154943},//121.76.0.0-121.77.255.255
                {2078801920, 2079064063},//123.232.0.0-123.235.255.255
                {-1950089216, -1948778497},//139.196.0.0-139.215.255.255
                {-1425539072, -1425014785},//171.8.0.0-171.15.255.255
                {-1236271104, -1235419137},//182.80.0.0-182.92.255.255
                {-770113536, -768606209},//210.25.0.0-210.47.255.255
                {-569376768, -564133889}, //222.16.0.0-222.95.255.255
        };
        //生成一个随机数
        Random random = new Random();
        int index = random.nextInt(10);
        String ip = numToip(range_ip[index][0] + new Random().nextInt(range_ip[index][1] - range_ip[index][0]));//获取ip

        return ip;
    }

    /**
     * 数字拼接成ip字符串
     *
     * @param ip
     * @return
     */
    private static String numToip(int ip) {
        int[] b = new int[4];

        b[0] = (ip >> 24) & 0xff;
        b[1] = (ip >> 16) & 0xff;
        b[2] = (ip >> 8) & 0xff;
        b[3] = ip & 0xff;
        String ip_str = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);
        return ip_str;
    }

    public static boolean gpsIsOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsIsOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gpsIsOpen;
    }


    public static final boolean havePermissionLessThan23(Context context, String permission) {
        int result = PermissionChecker.checkPermission(context, permission, android.os.Process.myPid(), android.os.Process.myUid(), context.getPackageName());
        Log.w("GPS", "havePermissionLessThan23:" + permission + " result:" + result);
        int resultCompat = ContextCompat.checkSelfPermission(context, permission);
        Log.w("GPS", "havePermissionLessThan23resultCompat:" + permission + " result:" + resultCompat);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    public static String getRealPathFromURI(Context context, Intent data) {
        String path = "";
        if (data != null) {
            if (data.getDataString().contains("content")) {
                Uri contentURI = data.getData();
                Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);

                if (cursor == null) {
                    path = contentURI.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cursor.getString(idx);
                }
                cursor.close();
            } else {
                path = data.getDataString().replace("file://", "");
            }
        }
        return path;
    }

    public static boolean checkCameraHardware(Context context) {
        // this device has a camera
// no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isHasSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static Uri getFileUrlSafety(Context context, String file) {
        return getFileUrlSafety(context, new File(file));
    }

    public static Uri getFileUrlSafety(Context context, File file) {
        Uri mUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } else
            mUri = Uri.fromFile(file);
        return mUri;
    }

    public static int getTransParent(Float value) {
        if(value>10.0F) {
            value=1.0F;
        } else if(value<0)
            value=0F;
        return Math.round(value*255);
    }

    public static int mergeColor(int rgb,int tran) {
        int color=(tran<<24)|(0x00FFFFFF&rgb);
        return color;
    }

    //针对非系统影音资源文件夹
    public static void insertIntoMediaStore(Context context, boolean isVideo, File saveFile, long createTime) {
        ContentResolver mContentResolver = context.getContentResolver();
        if (createTime == 0)
            createTime = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        //值一样，但是还是用常量区分对待
        values.put(isVideo ? MediaStore.Video.VideoColumns.DATE_TAKEN
                : MediaStore.Images.ImageColumns.DATE_TAKEN, createTime);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
        if (!isVideo)
            values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        values.put(MediaStore.MediaColumns.MIME_TYPE, isVideo ?"video/mp4" : "image/jpeg");
        //插入
        mContentResolver.insert(isVideo
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        scanFile(context,saveFile.getAbsolutePath());
    }

    /**
     * 针对系统文夹只需要扫描,不用插入内容提供者,不然会重复
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void scanFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }
}