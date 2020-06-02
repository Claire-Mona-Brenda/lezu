package com.konka.renting.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.konka.renting.KonkaApplication;
import com.konka.renting.base.BaseApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kaite on 2018/2/8.
 */

public class PictureUtils {
    /**
     * 从图片uri获取path
     *
     * @param context 上下文
     * @param uri     图片uri
     */
    public static String getPathFromUri(Context context, Uri uri) {
        String outPath = "";
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null);
        if (cursor == null) {
            // miui 2.3 有可能为null
            return uri.getPath();
        } else {
            if (uri.toString().contains("content://com.android.providers.media.documents/document/image")) { // htc 某些手机
                // 获取图片地址
                String _id = null;
                String uridecode = uri.decode(uri.toString());
                int id_index = uridecode.lastIndexOf(":");
                _id = uridecode.substring(id_index + 1);
                Cursor mcursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, " _id = " + _id,
                        null, null);
                mcursor.moveToFirst();
                int column_index = mcursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                outPath = mcursor.getString(column_index);
                if (!mcursor.isClosed()) {
                    mcursor.close();
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                return outPath;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (DocumentsContract.isDocumentUri(context, uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            //Log.d(TAG, uri.toString());
                            String id = docId.split(":")[1];
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            outPath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            //Log.d(TAG, uri.toString());
                            Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    Long.valueOf(docId));
                            outPath = getImagePath(context, contentUri, null);
                        }
                        return outPath;
                    }
                }
                if ("content".equalsIgnoreCase(uri.getScheme())) {
                    String auth = uri.getAuthority();
                    if (auth.equals("media")) {
                        outPath = getImagePath(context, uri, null);
                    } else if (auth.equals("com.pcb.mall.fileprovider")) {
                        //参看file_paths_public配置
                        outPath = Environment.getExternalStorageDirectory() + "/Pictures/" + uri.getLastPathSegment();
                    }
                    return outPath;
                }
            }
            return outPath;
        }

    }


    /**
     * 从uri中取查询path路径
     *
     * @param context   上下文
     * @param uri
     * @param selection
     */
    private static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    /**
     * 图片压缩 (AndroidQ以上)
     *
     * @param context context
     * @param uri uri
     * @return 压缩后的图片uri
     */
    private static Uri compressImageWithAndroidQ (Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null;
        }
        if (uri == null) {
            return null;
        }
        Uri insertUri = null;
        ContentResolver resolver = context.getContentResolver();
        ByteArrayOutputStream bos = null;
        OutputStream os = null;
        try {
            //1.uri转换bitmap类型并旋转图片为正常图片
            Bitmap tagBitmap = uriToBitmap(context, uri);

            //2.压缩图片并写入byteArrayOutputStream流中
            bos = new ByteArrayOutputStream();
            if (tagBitmap != null) {
                tagBitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos);
                tagBitmap.recycle();
            }
            //3.获取图片需要缓存的uri地址并copy到指定路径,主要通过MediaStore,请参照上篇文章
//            insertUri = insertImageFileIntoMediaStore("","*");
            if (insertUri == null) {
                return null;
            }
            os = resolver.openOutputStream(insertUri);
            if (os != null) {
                os.write(bos.toByteArray());
                os.flush();
            }
            //4.返回uri类型提供页面展示
            return insertUri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
        }
        return insertUri;
    }

    /**
     * AndroidQ以上保存图片到公共目录
     *
     * @param imageName 图片名称
     * @param imageType 图片类型
     * @param relativePath 缓存路径
     */
    private static Uri insertImageFileIntoMediaStore (String imageName, String imageType,
                                                      String relativePath) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null;
        }
        if (TextUtils.isEmpty(relativePath)) {
            return null;
        }
        Uri insertUri = null;
        ContentResolver resolver = KonkaApplication.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.DESCRIPTION, imageName);
        //设置文件类型为image/*
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + imageType);
        //注意：MediaStore.Images.Media.RELATIVE_PATH需要targetSdkVersion=29,
        //故该方法只可在Android10的手机上执行
        values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath);
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        insertUri = resolver.insert(external, values);
        return insertUri;
    }

    /**
     * AndroidQ以下
     * 创建图片缓存路径
     *
     * @param fileName 名称 包含文件类型
     * @return 返回file类型
     */
    public static File getImageFileCache (String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null;
        }
        //创建项目图片公共缓存目录
        File file = new File(Environment.getExternalStorageDirectory()+
                File.separator +
                Environment.DIRECTORY_PICTURES +
                File.separator +
                "testAndroidQ" +
                File.separator +
                "images");
        if (! file.exists()) {
            file.mkdirs();
        }
        //创建对应图片的缓存路径
        return new File(file + File.separator + fileName);
    }

    /**
     * uri转bitmap类型并旋转为正常图片
     *
     * @param context context
     * @param uri uri
     * @return 返回旋转后为正常图片的bitmap类型
     */
    private static Bitmap uriToBitmap (Context context, Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileDescriptor fileDescriptor = null;
        ExifInterface exifInterface = null;
        Bitmap tagBitmap = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");

            if (parcelFileDescriptor != null && parcelFileDescriptor.getFileDescriptor() != null) {
                fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                //1.转换uri为bitmap类型
                tagBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                //2.旋转图片
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    exifInterface = new ExifInterface(fileDescriptor);
                }
//                tagBitmap = rotatingImage(tagBitmap, exifInterface);
            }
            return tagBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
            }
        }
        return tagBitmap;
    }
}
