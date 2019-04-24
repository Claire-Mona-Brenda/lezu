package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 拍照bitmap处理
 * Created by 刘永毅 on 2017/7/26.
 */

public class PhotoBitmapUtil {
    //存放拍摄图片的文件夹
    private static final String HYPHOTO_FILES_NAME="/lefulyy/issue_bitmap";
    //获取的时间格式
    public static final String HYPHOTO_TIME_STYLE="yyyyMMddHHmmss";
    //图片种类
    public static final String HYPHOTO_IMAGE_TYPE=".png";

    /**
     * 压缩图片1/2 比例
     * @param path
     * @return
     */
    public static Bitmap getCompressPhoto(String path){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false;    //获取图片大小（原始宽度、高度）
        options.inSampleSize=2;   //图片的长和宽谁原理啊的1/2
        Bitmap bitmap= BitmapFactory.decodeFile(path,options);
        options=null;
        return bitmap;
    }

    /**
     * 获取手机存储路径
     */
    private static String getPhoneRootPAth(Context context){
        //是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                ||!Environment.isExternalStorageRemovable()){
            //获取SD卡根目录
            return context.getExternalCacheDir().getPath();
        }else {
            // 获取apk安装缓存路径
            return context.getCacheDir().getPath();
        }
    }
    public static String getPhotoFileName(Context context){
        File file=new File(getPhoneRootPAth(context)+HYPHOTO_FILES_NAME);
        //判断文件是否已存在，不存在则创建
        if (!file.exists()){
            file.mkdirs();
        }
        //设置图片文件名称
        SimpleDateFormat format=new SimpleDateFormat(HYPHOTO_TIME_STYLE, Locale.getDefault());
        Date date=new Date(System.currentTimeMillis());
        String time=format.format(date);
        String photoName="/"+time+HYPHOTO_IMAGE_TYPE;
        return file+photoName;
    }

    /**
     * 保存bitmap 图片在SD 卡中
     * 如果没有SD 卡保存在手机中
     * @param bitmap   需要保存的bitmap图片
     * @param context
     * @return  成功时返回路径，失败是返回null
     */
    public static String savePhotoToSD(Bitmap bitmap, Context context){
        FileOutputStream outputStream=null;
        String fileName=getPhotoFileName(context);
        try {
            outputStream=new FileOutputStream(fileName);    //将路径写入文件夹
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap!=null){
                bitmap.recycle();
            }
        }
    }

    /**
     * 删除图片缓存
     * @param context
     */
    public static void DelFilePhoto(Context context){
        File file=new File(getPhoneRootPAth(context)+HYPHOTO_FILES_NAME);
        File[] files=file.listFiles();
        for (int i=0;i<file.length();i++){
            File photoFile=new File(files[i].getPath());
            photoFile.delete();
        }
    }
    public static String amendRotatePhoto(String originpath, Context context){
        //取得图片旋转角度
        int angle=readPictureDegree(originpath);
        //把原图压缩后得到Bitmap对象
        Bitmap bmp=getCompressPhoto(originpath);

        //修复图片旋转的角度
        Bitmap bitmap=rotaingImageView(angle,bmp);
        // 保存修复后的图片并返回保存后的文件路径
        return savePhotoToSD(bitmap,context);
    }

    private static Bitmap rotaingImageView(int angle, Bitmap bmp) {
        Bitmap returnBtimap=null;
        //根据旋转角度，生成旋转矩阵
        Matrix matrix=new Matrix();
        matrix.postRotate(angle);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBtimap= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        }catch (OutOfMemoryError e) {
        }
        if (returnBtimap == null) {
            returnBtimap=bmp;
        }
        if (bmp != returnBtimap) {
            bmp.recycle();
        }
        return returnBtimap;
    }

    /**
     * 读取照片旋转角度
     * @param originpath   照片路径
     * @return  角度
     */
    private static int readPictureDegree(String originpath) {
        int degree=0;
        try {
            ExifInterface exifInterface=new ExifInterface(originpath);
            int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION
                    , ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree=90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree=180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree=270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
