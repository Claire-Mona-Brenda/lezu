package com.konka.renting.landlord.home.more;

import android.content.Context;
import android.content.Intent;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;

import butterknife.OnClick;

public class MoreHotRoomListActivity extends BaseActivity {

    public static void toActivity(Context context) {
        Intent intent = new Intent(context, MoreHotRoomListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_more_hot_room_list;
    }

    @Override
    public void init() {
        setTitleText(R.string.landlord_home_hot_house);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, MoreRoomListFragment.newInstance()).commit();
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
