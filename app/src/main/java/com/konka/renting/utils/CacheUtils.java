package com.konka.renting.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.konka.renting.KonkaApplication;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class CacheUtils {
    private static final String FILE_NAME = "lezu";
    private static final String CACHE_PATH = FILE_NAME + File.separator + "pic_cache" + File.separator;
    private static final String CACHE_PATH_Q = File.separator + "pic_cache" + File.separator;

    public static boolean isHavePermission = false;

    /**
     * 获取缓存图片文件
     */
    public static File getFile(String url) {
        if (TextUtils.isEmpty(url))
            return null;
        String[] s = url.split("/");
        String name = s[s.length - 1];
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File PICTURES = KonkaApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            path = PICTURES.getAbsolutePath() + CACHE_PATH_Q + name;
        } else {
            path = getSDCardPath() == null ? null : getSDCardPath() + CACHE_PATH + name;
        }
        if (path == null)
            return null;
        File file = new File(path);
        if (file.exists())
            return file;
        return null;
    }

    /**
     * 下载缓存图片文件
     */
    public static boolean saveFile(String url, Context context) {
        if (TextUtils.isEmpty(url) || !isHavePermission)
            return false;
        String[] s = url.split("/");
        String name = s[s.length - 1];
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File PICTURES = KonkaApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            path = PICTURES.getAbsolutePath() + CACHE_PATH_Q + name;
        } else {
            path = getSDCardPath() == null ? null : getSDCardPath() + CACHE_PATH + name;
        }
        if (path == null)
            return false;
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                FileOutputStream ostream = null;
                try {
                    ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
//        long maxSize = Runtime.getRuntime().maxMemory() / 8;//设置图片缓存大小为运行时缓存的八分之一
//        OkHttpClient client = new OkHttpClient.Builder()
//                .cache(new Cache(file.getParentFile(), maxSize))
//                .build();
//
//        Picasso picasso = new Picasso.Builder(context)
//                .downloader(new OkHttp3Downloader(client))//注意此处替换为 OkHttp3Downloader
//                .build();
        Picasso.get().load(url).into(target);
        return false;
    }


    /**
     * 检查缓存图片文件是否存在
     */
    public static boolean checkFileExist(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        String[] s = url.split("/");
        String name = s[s.length - 1];
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File PICTURES = KonkaApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            path = PICTURES.getAbsolutePath() + CACHE_PATH_Q + name;
        } else {
            path = getSDCardPath() == null ? null : getSDCardPath() + CACHE_PATH + name;
        }

        if (path == null)
            return false;
        File file = new File(path);
        if (file.exists())
            return true;
        return false;
    }

    /**
     * 获取sd卡路径
     */
    public static String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals
                (Environment.MEDIA_SHARED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        if (sdcardDir != null) {
            return sdcardDir.toString() + File.separator;
        } else {
            return null;
        }
    }
}
