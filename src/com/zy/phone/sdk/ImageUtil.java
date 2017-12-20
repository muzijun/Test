package com.zy.phone.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Õº∆¨—°‘Ò/≈ƒ’’
 *
 * @Author KenChung
 */
public class ImageUtil {

    private static final String TAG ="ImageUtil";

    
    public static Intent choosePicture(){
    	if (Build.VERSION.SDK_INT > 25) {
//    		 Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
//    	     contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//    	     contentSelectionIntent.setType("image/*");
//
//    	     Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//    	     chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//    	     chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
//    	     return chooserIntent;
       		Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
    		albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
    		return Intent.createChooser(albumIntent, null);

    	}else{
    		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    	    // intent.setType("image/*");
    		Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
    		albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
    		return Intent.createChooser(albumIntent, null);
    	    //return Intent.createChooser(intent, null);
    	}
    	
    }

    /**
     * ≈ƒ’’∫Û∑µªÿ
     */
    public static Intent takeBigPicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, newPictureUri(getNewPhotoPath()));
        return intent;
    }

    public static String getDirPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/WebViewUploadImage";
    }

    private static String getNewPhotoPath() {
        return getDirPath() + "/" + System.currentTimeMillis() + ".jpg";
    }

    public static String retrievePath(Context context, Intent sourceIntent, Intent dataIntent) {
        String picPath = "";
        try {
            Uri uri;
            if (dataIntent != null) {
                uri = dataIntent.getData();
                if (uri != null) {
                    picPath = ContentUtil.getPath(context, uri);
                }
                if (isFileExists(picPath)) {
                    return picPath;
                }

                Log.w(TAG, String.format("retrievePath failed from dataIntent:%s, extras:%s", dataIntent, dataIntent.getExtras()));
            }

            if (sourceIntent != null) {
                uri = sourceIntent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                if (uri != null) {
                    String scheme = uri.getScheme();
                    if (scheme != null && scheme.startsWith("file")) {
                        picPath = uri.getPath();
                    }
                }
                if (!TextUtils.isEmpty(picPath)) {
                    File file = new File(picPath);
                    if (!file.exists() || !file.isFile()) {
                        Log.w(TAG, String.format("retrievePath file not found from sourceIntent path:%s", picPath));
                    }
                }
            }
            return picPath;
        } finally {
            Log.d(TAG, "retrievePath(" + sourceIntent + "," + dataIntent + ") ret: " + picPath);
        }
    }

    private static Uri newPictureUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private static boolean isFileExists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        return true;
    }
}
