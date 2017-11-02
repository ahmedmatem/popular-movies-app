package com.example.android.popularmoviesapp.utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ahmed on 27/10/2017.
 */

public class StorageUtils {

    public static final String IMAGE_DIR = "images";

    /**
     * This method save a bitmap image to internal memory
     *
     * @param context
     * @param image
     * @param name
     * @return image absolute path
     */
    public static String saveToInternalStorage(Context context, Bitmap image, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/<yourAppName>/app_data/imageDir
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        // Create image directory
        File path = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }

    public static boolean deleteImageFromStorage(Context context, String url){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/<yourAppName>/app_data/imageDir
        File dir = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File file = new File(url);
        boolean deleted = file.delete();
        return deleted;
    }

    public static Bitmap getImageFromStorage(String path, String title) {
        Bitmap bitmap = null;
        try {
            File f = new File(path, title);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Uri buildImageUri(String imageUrl){
        File file = new File(imageUrl);
        return Uri.fromFile(file);
    }

    public static String getPathLastSegment(String path){
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }
}
