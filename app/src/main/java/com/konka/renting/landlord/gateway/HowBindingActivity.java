package com.konka.renting.landlord.gateway;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.HowBean;
import com.konka.renting.landlord.gateway.widget.HowBindPopup;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HowBindingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.activity_how_bind_recycler)
    RecyclerView mRecyclerView;


    CommonAdapter<HowBean> commonAdapter;
    List<HowBean> data = new ArrayList<>();
    HowBindPopup howBindPopup;


    public static void toActivity(Context context) {
        Intent intent = new Intent(context, HowBindingActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_how_binding;
    }

    @Override
    public void init() {
        tvTitle.setText(R.string.title_how_to_bind);

        commonAdapter = new CommonAdapter<HowBean>(this, data, R.layout.adapter_how_bind) {
            @Override
            public void convert(final ViewHolder viewHolder, final HowBean bean) {
                viewHolder.setText(R.id.adapter_how_bind_tv_name, bean.getName());
                viewHolder.setImageResource(R.id.adapter_how_bind_img, bean.getImgId());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showHowBindPopup(bean.getIndex());
                    }
                });
            }
        };
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(commonAdapter);


        initData();


    }

    private void initData() {
//        data.add(new HowBean(0,getString(R.string.how_bind_device_name_1), R.mipmap.k44_72pt));
        data.add(new HowBean(1,getString(R.string.how_bind_device_name_2), R.mipmap.k52_72pt));
//        data.add(new HowBean(2,getString(R.string.how_bind_device_name_3), R.mipmap.k47_72pt));
//        data.add(new HowBean(3,getString(R.string.how_bind_device_name_4), R.mipmap.safebox_72pt));
//        data.add(new HowBean(4,getString(R.string.how_bind_device_name_5), R.mipmap.pm25_72pt));
        commonAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    private void showHowBindPopup(int position) {
        if (howBindPopup == null)
            howBindPopup = new HowBindPopup(this);
        howBindPopup.chooseItem(position);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        howBindPopup.showAtLocation((View) tvTitle.getParent(), Gravity.CENTER,0,0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        howBindPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (howBindPopup!=null)
            howBindPopup.onDestroy();
    }
}
