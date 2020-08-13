package com.rt.rtsl.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.RawRes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsManager
{
	public static Bitmap getImageFromAssetsFile(Context context, String fileName)
	{
		Bitmap bitmap = null;
		AssetManager assetManager = context.getResources().getAssets();
		try {
			InputStream is = assetManager.open(fileName);
			BitmapFactory.Options o = new BitmapFactory.Options();
			bitmap = BitmapFactory.decodeStream(is,null,o);
			is.close();
		} catch (Exception e) {
			return null;
		}
		return bitmap;
	}

	/**
	 * 从assest目录下取下面的文件
	 * @param context
	 * @param path
	 * @return
	 */
	public static String[] getAssetsFilesNameFromPath(Context context,String path)
	{
		AssetManager assetManager = context.getResources().getAssets();
		try {
			return assetManager.list(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[0];
	}
	public static String getTextFromAssetsFile(Context context,String fileName)
	{
		String strText="";
		AssetManager assetManager = context.getResources().getAssets();
		BufferedInputStream bufferedInputStream=null;
		InputStreamReader reader=null;
		BufferedReader bufferedReader=null;
		InputStream is=null;
		try {
			is = assetManager.open(fileName);
			bufferedInputStream = new BufferedInputStream(is);
			reader = new InputStreamReader( bufferedInputStream, "UTF-8" );
			bufferedReader = new BufferedReader( reader );
			StringBuilder builder=new StringBuilder();
			while ( ( strText = bufferedReader.readLine() ) != null) {
				builder.append( strText+"\n" );
			}

			strText = builder.toString();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if (null != bufferedReader) 
				{
					bufferedReader.close();
					bufferedReader = null;
				}
				if (null != bufferedInputStream)
				{
					bufferedInputStream.close();
					bufferedInputStream = null;
				}
				if (null != is) {
					is.close();
					is = null;
				}
				if(reader!=null)
				{
					reader.close();
					reader=null;
				}
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return strText;
	}
	
	public static InputStream getAssestFileStream(Context context,String fileName)
	{
		try {
			return context.getAssets().open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getRawFileStream(Context context,@RawRes int id)
	{
		try {
			return context.getResources().openRawResource(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean copyAssetFile2SDCard(Context context,String fileName,File sdcardFile)
	{
		try
		{
			if(!FileUtil.isFileEnable(sdcardFile))
			{
				FileUtil.createFile(sdcardFile.getAbsolutePath());
			}
			return FileUtil.copyFile(getAssestFileStream(context, fileName), new FileOutputStream(sdcardFile, false));
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return false;
	}

	public static boolean copyRawFile2SDCard(Context context,@RawRes int id,String sdcardFile)
	{
		try
		{
			if(!FileUtil.isFileEnable(sdcardFile))
			{
				FileUtil.createFile(sdcardFile);
			}
			return FileUtil.copyFile(getRawFileStream(context, id), new FileOutputStream(sdcardFile, false));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}