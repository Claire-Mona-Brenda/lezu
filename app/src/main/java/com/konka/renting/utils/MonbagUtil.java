package com.konka.renting.utils;

import android.content.Context;

import com.konka.renting.bean.UserInfoBean;
import com.konka.renting.landlord.house.data.MonBagRequest;
import com.konka.renting.landlord.user.userinfo.FaceDetectActivity;
import com.konka.renting.landlord.user.userinfo.IdentyActivity;

/**
 * Created by jbl on 2018/5/10.
 */

public class MonbagUtil {

    /**
     * tent_land 0租客，1房东
     */
    public static void monbag(final Context ctx, final StatusSuccess statusSuccess) {
        final boolean status = false;
        MonBagRequest checkUserRequest = new MonBagRequest(ctx, new MonBagRequest.IUserCheck() {
            @Override
            public void userCall(Object obj) {

                UserInfoBean userInfoBean = (UserInfoBean) obj;

                statusSuccess.success(userInfoBean);


            }

        });

        checkUserRequest.getLandord();


    }

    public interface StatusSuccess {
        public void success(Object obj);
    }

}
