package com.konka.renting.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

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

}
