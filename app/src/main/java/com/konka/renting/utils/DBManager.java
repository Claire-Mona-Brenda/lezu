package com.konka.renting.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.konka.renting.bean.MapLocationSearchBean;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    Context mContext;
    DBHelper dbHelper;
    private SQLiteDatabase mDatabase;

    public DBManager(Context mContext) {
        this.mContext = mContext;
        dbHelper = new DBHelper(mContext);
        mDatabase = dbHelper.getWritableDatabase();
    }


    // 表名
    // null。数据库如果插入的数据为null，会引起数据库不稳定。为了防止崩溃，需要传入第二个参数要求的对象
    // 如果插入的数据不为null，没有必要传入第二个参数避免崩溃，所以为null
    // 插入的数据
    public void insertSearchHistoryData(String name, String address, String lat, String lng, String level, String count) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TABLE_SEARCH_NAME, name);
        values.put(DBHelper.TABLE_SEARCH_ADDRESS, address);
        values.put(DBHelper.TABLE_SEARCH_LAT, lat);
        values.put(DBHelper.TABLE_SEARCH_LNG, lng);
        values.put(DBHelper.TABLE_SEARCH_LEVEL, level);
        values.put(DBHelper.TABLE_SEARCH_COUNT, count);
        mDatabase.insert(DBHelper.TABLE_NAME, null, values);
    }

    // 表名
    // 删除条件
    // 满足删除的值
    public void deleteSearchHistoryData(String name) {
        mDatabase.delete(DBHelper.TABLE_NAME, DBHelper.TABLE_SEARCH_NAME + " = ?", new String[]{name});
    }

    // 表名
    // 修改后的数据
    // 修改条件
    // 满足修改的值
    public void updateSearchHistoryData(String name, String address, String lat, String lng, String level, String count) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TABLE_SEARCH_NAME, name);
        values.put(DBHelper.TABLE_SEARCH_ADDRESS, address);
        values.put(DBHelper.TABLE_SEARCH_LAT, lat);
        values.put(DBHelper.TABLE_SEARCH_LNG, lng);
        values.put(DBHelper.TABLE_SEARCH_LEVEL, level);
        values.put(DBHelper.TABLE_SEARCH_COUNT, count);
        mDatabase.update(DBHelper.TABLE_NAME, values, DBHelper.TABLE_SEARCH_NAME + " = ?", new String[]{name});
    }

    // 表名
    // 查询字段
    // 查询条件
    // 满足查询的值
    // 分组
    // 分组筛选关键字
    // 排序
    public MapLocationSearchBean querySearchHistoryData(String search) {
        MapLocationSearchBean searchBean = null;
        Cursor cursor = mDatabase.rawQuery("select * from " + DBHelper.TABLE_NAME + " where " + DBHelper.TABLE_SEARCH_NAME + "= ?", new String[]{search});
        int nameIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_NAME);
        int addressIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_ADDRESS);
        int latIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_LAT);
        int lngIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_LNG);
        int levelIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_LEVEL);
        int countIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_COUNT);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String address = cursor.getString(addressIndex);
            String lat = cursor.getString(latIndex);
            String lng = cursor.getString(lngIndex);
            String level = cursor.getString(levelIndex);
            String count = cursor.getString(countIndex);
            searchBean=new MapLocationSearchBean();
            searchBean.setName(name);
            searchBean.setAddress(address);
            searchBean.setLat(lat);
            searchBean.setLng(lng);
            searchBean.setLevel(TextUtils.isEmpty(level) ? 0 : Integer.valueOf(level));
            searchBean.setCount(TextUtils.isEmpty(count) ? 0 : Integer.valueOf(count));

        }
        cursor.close();
        return searchBean;

    }

    public List<MapLocationSearchBean> queryAlllSearchHistoryData() {
        ArrayList<MapLocationSearchBean> list = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("select * from " + DBHelper.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_NAME);
            int addressIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_ADDRESS);
            int latIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_LAT);
            int lngIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_LNG);
            int levelIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_LEVEL);
            int countIndex = cursor.getColumnIndex(DBHelper.TABLE_SEARCH_COUNT);
            String name = cursor.getString(nameIndex);
            String address = cursor.getString(addressIndex);
            String lat = cursor.getString(latIndex);
            String lng = cursor.getString(lngIndex);
            String level = cursor.getString(levelIndex);
            String count = cursor.getString(countIndex);
            MapLocationSearchBean searchBean = new MapLocationSearchBean();
            searchBean.setName(name);
            searchBean.setAddress(address);
            searchBean.setLat(lat);
            searchBean.setLng(lng);
            searchBean.setLevel(TextUtils.isEmpty(level) ? 0 : Integer.valueOf(level));
            searchBean.setCount(TextUtils.isEmpty(count) ? 0 : Integer.valueOf(count));
            list.add(searchBean);
        }
        cursor.close();
        return list;
    }
}
