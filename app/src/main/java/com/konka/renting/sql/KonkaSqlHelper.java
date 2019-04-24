package com.konka.renting.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.konka.renting.bean.GatewayInfo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jzxiang on 6/17/17.
 */

public class KonkaSqlHelper extends OrmLiteSqliteOpenHelper {


    private volatile static KonkaSqlHelper instance;


    public static final String DATABASE = "konka.db";
    public static final int VERSION = 1;

    private Map<String, Dao> mDaos = new HashMap<>();

    public KonkaSqlHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, GatewayInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    public void deleteTable(Class clazz){
        try {
            TableUtils.clearTable(getConnectionSource(),clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao getDao(Class clazz) throws SQLException {
        String clazzName = clazz.getSimpleName();

        if (mDaos.containsKey(clazzName))
            return mDaos.get(clazzName);

        Dao dao = super.getDao(clazz);
        mDaos.put(clazzName, dao);

        return dao;
    }

    @Override
    public void close() {
        super.close();
        Set<String> keyset = mDaos.keySet();
        for (String key : keyset) {
            Dao dao = mDaos.get(key);
            dao = null;
        }
        mDaos.clear();
    }

    public static KonkaSqlHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (KonkaSqlHelper.class) {
                if (instance == null)
                    instance = new KonkaSqlHelper(context.getApplicationContext());
            }
        }

        return instance;
    }
}
