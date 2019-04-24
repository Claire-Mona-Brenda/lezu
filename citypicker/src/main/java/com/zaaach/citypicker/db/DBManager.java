package com.zaaach.citypicker.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.zaaach.citypicker.model.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.zaaach.citypicker.db.DBConfig.DB_NAME_V1;
import static com.zaaach.citypicker.db.DBConfig.LATEST_DB_NAME;

/**
 * Author Bro0cL on 2016/1/26.
 */
public class DBManager {
    private static final int BUFFER_SIZE = 1024;

    private String DB_PATH;
    private Context mContext;

    private List<City> mAllCitys = new ArrayList<>();

    public DBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "databases" + File.separator;
//        copyDBFile();

        getData(context);
    }

    private void getData(Context context) {
        String data = getJson(context, "city.json");
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String name = jsonObject.optString("name");
                String index = jsonObject.optString("index");
                City city = new City(name, "", index, index);
                mAllCitys.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(mAllCitys, new CityComparator());
    }

    private void copyDBFile() {
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //如果旧版数据库存在，则删除
        File dbV1 = new File(DB_PATH + DB_NAME_V1);
        if (dbV1.exists()) {
            dbV1.delete();
        }
        //创建新版本数据库
        File dbFile = new File(DB_PATH + LATEST_DB_NAME);
        if (!dbFile.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(LATEST_DB_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<City> getAllCities() {
        return mAllCitys;
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + LATEST_DB_NAME, null);
//        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
//        List<City> result = new ArrayList<>();
//        City city;
//        while (cursor.moveToNext()) {
//            String name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME));
//            String province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE));
//            String pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN));
//            String code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE));
//            city = new City(name, province, pinyin, code);
//            result.add(city);
//        }
//        cursor.close();
//        db.close();
//        Collections.sort(result, new CityComparator());
//        return result;
    }

    public List<City> searchCity(String keyword) {
//        String sql = "select * from " + TABLE_NAME + " where "
//                + COLUMN_C_NAME + " like ? " + "or "
//                + COLUMN_C_PINYIN + " like ? ";
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + LATEST_DB_NAME, null);
//        Cursor cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%", keyword + "%"});
//
//        List<City> result = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            String name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME));
//            String province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE));
//            String pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN));
//            String code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE));
//            City city = new City(name, province, pinyin, code);
//            result.add(city);
//        }
//        cursor.close();
//        db.close();
//        CityComparator comparator = new CityComparator();
//        Collections.sort(result, comparator);
        keyword = keyword.toUpperCase();
        List<City> result = new ArrayList<>();
        for (City allCity : mAllCitys) {
            if (allCity.getName().contains(keyword))
                result.add(allCity);
            else if (allCity.getPinyin().contains(keyword))
                result.add(allCity);
        }

        return result;
    }

    /**
     * sort by a-z
     */
    public class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }

    //读取方法
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
