package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.event.BindDevSuccessEvent;
import com.konka.renting.landlord.house.view.HousePublishActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class AddHouseCompleteActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.lin_title)
    FrameLayout linTitle;
    @BindView(R.id.activity_add_house_complete_img_pic)
    ImageView mImgPic;
    @BindView(R.id.activity_add_house_complete_tv_name)
    TextView mTvName;
    @BindView(R.id.activity_add_house_complete_tv_info)
    TextView mTvInfo;
    @BindView(R.id.activity_add_house_complete_btn_bind)
    Button mBtnBind;
    @BindView(R.id.activity_add_house_complete_btn_rent)
    Button mBtnRent;

    String url;
    String address;
    String room_type;
    String area;
    String floor;
    String room_id;

    public static void toActivity(Context context, String url, String address, String room_type, String area, String floor, String room_id) {
        Intent intent = new Intent(context, AddHouseCompleteActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("address", address);
        intent.putExtra("room_type", room_type);
        intent.putExtra("area", area);
        intent.putExtra("floor", floor);
        intent.putExtra("room_id", room_id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_house_complete;
    }

    @Override
    public void init() {
        url = getIntent().getStringExtra("url");
        address = getIntent().getStringExtra("address");
        room_type = getIntent().getStringExtra("room_type");
        area = getIntent().getStringExtra("area");
        floor = getIntent().getStringExtra("floor");
        room_id = getIntent().getStringExtra("room_id");

        tvTitle.setText(R.string.add_house);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);

        if (!TextUtils.isEmpty(url)) {
            Picasso.get().load(url).error(R.mipmap.fangchan_jiazai).into(mImgPic);
        } else {
            Picasso.get().load(R.mipmap.fangchan_jiazai).into(mImgPic);
        }
        mTvName.setText(address);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        String[] type = room_type.split("_");
        spannableStringBuilder.append( type[0] + "室" + type[2] + "厅" + (type[1].equals("0") ? "" : type[1] + "卫") + "/");
        spannableStringBuilder.append( getArea(area));
        spannableStringBuilder.append( "/" + floor + "楼");
        mTvInfo.setText(spannableStringBuilder);

        addRxBusSubscribe(BindDevSuccessEvent.class, new Action1<BindDevSuccessEvent>() {
            @Override
            public void call(BindDevSuccessEvent bindDevSuccessEvent) {
                finish();
            }
        });

    }


    @OnClick({R.id.iv_back, R.id.activity_add_house_complete_btn_bind, R.id.activity_add_house_complete_btn_rent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.activity_add_house_complete_btn_bind:
                DevListActivity.toActivity(this, room_id, 1, true, true);
                break;
            case R.id.activity_add_house_complete_btn_rent:
                HousePublishActivity.toActivity(this, room_id);
                break;
        }
    }

    private SpannableStringBuilder getArea(String area) {
        SpannableString m2 = new SpannableString("m2");
        m2.setSpan(new RelativeSizeSpan(0.5f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//一半大小
        m2.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   //上标

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(area);
        spannableStringBuilder.append(m2);

        return spannableStringBuilder;

    }
}
