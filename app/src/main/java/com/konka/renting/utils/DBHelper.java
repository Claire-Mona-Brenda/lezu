package com.konka.renting.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lezu.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "search_room_history";
    public static final String TABLE_SEARCH_NAME = "name";
    public static final String TABLE_SEARCH_ADDRESS = "address";
    public static final String TABLE_SEARCH_LAT = "lat";
    public static final String TABLE_SEARCH_LNG = "lng";
    public static final String TABLE_SEARCH_LEVEL = "level";
    public static final String TABLE_SEARCH_COUNT = "count";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                TABLE_SEARCH_NAME + " TEXT, "+
                TABLE_SEARCH_ADDRESS+" TEXT,"+
                TABLE_SEARCH_LAT+" TEXT,"+
                TABLE_SEARCH_LNG+" TEXT,"+
                TABLE_SEARCH_LEVEL+" TEXT,"+
                TABLE_SEARCH_COUNT + " TEXT)");//建表，对数据库进行操作等
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
