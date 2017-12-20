package com.zy.phone;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

public class MyFile {
	
	public static boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	public long getSDFreeSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long freeBlocks = sf.getAvailableBlocks();
		
		return (freeBlocks * blockSize) / 1024 / 1024; 
	}

	public long getSDAllSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long allBlocks = sf.getBlockCount();
		return (allBlocks * blockSize) / 1024 / 1024;
	}
	public static boolean existFile(String filepath) {
		File dirFile = new File(filepath);
		if (dirFile.exists()) {
			return true;
		}
		return false;

	}
	public static void saveFile(Bitmap bm, String fileName, String fileTarget) {
		String filePath = fileTarget;
		File dirFile = new File(filePath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File TypImageFile = new File(filePath + fileName);
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(TypImageFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void foundFilePath(String filepath) {
		try {
			File dirFile = new File(filepath);
			dirFile.mkdirs();
		} catch (Exception e) {
			
		}
		
	}

	private static boolean deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			}
			else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
			return true;
		}
		return false;
	}
	public static boolean deleteFile(String filepath) {
		File dirFile = new File(filepath);
		if (deleteFile(dirFile) == true) {
			return true;
		}
		return false;

	}
	public static Bitmap getSDFileBitmap(String filepath) {
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(filepath);
		return bitmap;

	}
	public static Drawable getSDFileDrawable(String filepath) {
		Drawable drawable = null;
		Bitmap bitmap = BitmapFactory.decodeFile(filepath);
		drawable = new BitmapDrawable(bitmap);
		return drawable;

	}
	public static boolean copyApkFromAssets(Context context, String fileName,
			String path) {
		boolean copyIsFinish = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return copyIsFinish;
	}
}
