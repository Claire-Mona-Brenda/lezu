package com.konka.renting.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Nate on 2015/9/10. SD卡相关功能辅助类
 */
public class SDCardUtils {
    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }


    /**
     * 判断sd中是否存在目录
     */
    public static boolean isDirExist(String dirPath) {
        File file = new File(getSDCardPath() + dirPath + File.separator);
        return file.exists();
    }

    /**
     * sd创建文件
     */
    public static File createSDDirs(String dirName) {
        File dir = new File(getSDCardPath() + dirName + File.separator);
        dir.mkdirs();
        return dir;
    }

    /**
     * saveBitmap
     */
    public static void saveBitmap(String dirpath, Bitmap bitmap, boolean isDelete) {
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dirpath);
        // 若存在即删除-默认只保留一张
        if (isDelete) {
            if (file.exists()) {
                file.delete();
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将一个object保存在一个特定的路径下面
     *
     * @param path
     * @param saveObject
     */
    public static final void saveObject(String path, Object saveObject) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File f = new File(path);
        try {
            fos = new FileOutputStream(f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(saveObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param path
     * @return 从特定的路径读取保存的object对象
     */
    public static final Object restoreObject(String path) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        Object object = null;
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        try {
            fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
            return object;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}
