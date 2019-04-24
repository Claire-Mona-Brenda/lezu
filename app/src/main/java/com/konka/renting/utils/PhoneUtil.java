package com.konka.renting.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by jbl on 18-4-24.
 */

public class PhoneUtil {
    public static void call(String phone,Context c) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c. startActivity(intent);
    }

    public static String hindPhone(String tel) {
        if (!tel.equals("")) {
            int len = tel.length();
            String str = tel.substring(0, 3);
            for (int i = 3; i < len - 2; i++) {
                str += "*";
            }
            str += tel.substring(len - 2, len);
            tel = str;
        }
        return tel;
    }
}
