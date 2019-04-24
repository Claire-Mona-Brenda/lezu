package com.konka.renting.landlord.home.search;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by jzxiang on 26/03/2018.
 */

public class SpHistoryStorage extends BaseHistoryStorage {
    private Context context;
    private int HISTORY_MAX;

    public static final String SEARCH_HISTORY = "konka_history";
    private static SpHistoryStorage instance;

    private SpHistoryStorage(Context context, int historyMax) {
        this.context = context.getApplicationContext();
        this.HISTORY_MAX = historyMax;
    }

    public static synchronized SpHistoryStorage getInstance(Context context, int historyMax) {
        if (instance == null) {
            synchronized (SpHistoryStorage.class) {
                if (instance == null) {
                    instance = new SpHistoryStorage(context, historyMax);
                }
            }
        }
        return instance;
    }

    private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddHHmmss");

//    public String generateKey() {
//        return mFormat.format(new Date());
//    }

    @Override
    public void save(String value) {
        Map<String, String> historys = (Map<String, String>) getAll();
        for (Map.Entry<String, String> entry : historys.entrySet()) {
            if (value.equals(entry.getValue())) {
                remove(entry.getKey());
            }
        }

        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(generateKey(),value);
        editor.commit();
    }

    @Override
    public void remove(String key) {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    @Override
    public void clear() {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public String generateKey() {
        return mFormat.format(new Date());
    }

    @Override
    public ArrayList<SearchHistoryInfo> sortHistory() {
        Map<String, ?> allHistory = getAll();
        ArrayList<SearchHistoryInfo> mResults = new ArrayList<>();
        Map<String, String> hisAll = (Map<String, String>) getAll();
        //将key排序升序
        Object[] keys = hisAll.keySet().toArray();
        Arrays.sort(keys);
        int keyLeng = keys.length;
        //这里计算 如果历史记录条数是大于 可以显示的最大条数，则用最大条数做循环条件，防止历史记录条数-最大条数为负值，数组越界
        int hisLeng = keyLeng > HISTORY_MAX ? HISTORY_MAX : keyLeng;
        for (int i = 1; i <= hisLeng; i++) {
            mResults.add(new SearchHistoryInfo((String) keys[keyLeng - i], hisAll.get(keys[keyLeng - i])));
        }
        return mResults;
    }

    private Map<String, ?> getAll() {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }
}
