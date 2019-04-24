package com.konka.renting.utils;

import android.content.Context;

import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.TenantUserinfoBean;
import com.konka.renting.bean.UserInfoBean;
import com.konka.renting.landlord.house.data.CheckUserRequest;
import com.konka.renting.landlord.user.userinfo.IdentyActivity;
import com.konka.renting.landlord.user.userinfo.NewFaceDectectActivity;

/**
 * Created by jbl on 2018/5/10.
 */

public class FaceDectacUtil {
    public enum FACETYPE {
        TENANT,
        LANDLORD
    }

    /**
     * tent_land 0租客，1房东
     */
    public static void faceDectac(final Context ctx, final FACETYPE tent_land, final StatusSuccess statusSuccess) {
        final boolean status = false;
        CheckUserRequest checkUserRequest = new CheckUserRequest(ctx, new CheckUserRequest.IUserCheck() {
            @Override
            public void userCall(Object obj) {
                if (!LoginUserBean.getInstance().getIs_lodge_identity().equals("1"))
                    NewFaceDectectActivity.toActivity(ctx, 1);
            }

        });
        if (tent_land == FACETYPE.TENANT) {
            checkUserRequest.checkTent();
        } else {
            checkUserRequest.checkLandord();
        }

//        LoginUserBean.getInstance().get

    }

    public interface StatusSuccess {
        public void success();
    }

}
