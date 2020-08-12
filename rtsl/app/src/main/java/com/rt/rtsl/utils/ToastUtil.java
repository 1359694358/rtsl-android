package com.rt.rtsl.utils;
import android.content.Context;
import android.widget.Toast;


public class ToastUtil {
	public static void show(Context context,String content)
	{
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
	public static void show(Context context,int resId)
	{
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
	
	public static void show(Context context,int resId ,int gravity)
	{
		Toast toast=Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.setGravity(gravity, 0, 0);
		toast.show();
	}
	public static void show(Context context,String resId ,int gravity)
	{
		Toast toast=Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.setGravity(gravity, 0, 0);
		toast.show();
	}

}
