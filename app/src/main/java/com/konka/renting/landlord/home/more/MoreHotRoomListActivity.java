package com.konka.renting.landlord.home.more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.RenterSearchListBean;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.tenant.findroom.roominfo.RoomInfoActivity;
import com.konka.renting.utils.RxUtil;
import com.mcxtzhang.commonadapter.rv.CommonAdapter;
import com.mcxtzhang.commonadapter.rv.ViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class MoreHotRoomListActivity extends BaseActivity {

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
    @BindView(R.id.activity_more_hot_room_list_rv_refresh)
    RecyclerView mRvRefresh;
    @BindView(R.id.activity_more_hot_room_list_srl_refresh)
    SmartRefreshLayout mSrlRefresh;

    String city;
    int rent_type;
    int type;
    int page = 1;

    private List<RenterSearchListBean> mRoomInfos = new ArrayList<>();
    CommonAdapter<RenterSearchListBean> mAdapter;


    public static void toActivity(Context context, String title, String city, int rent_type, int type) {
        Intent intent = new Intent(context, MoreHotRoomListActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("city", city);
        intent.putExtra("rent_type", rent_type);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_more_hot_room_list;
    }

    @Override
    public void init() {
        String title = getIntent().getStringExtra("title");
        city = getIntent().getStringExtra("city");
        rent_type = getIntent().getIntExtra("rent_type", 0);
        type = getIntent().getIntExtra("type", 0);

        setTitleText(title);

        mAdapter = new CommonAdapter<RenterSearchListBean>(this, mRoomInfos, R.layout.item_landlord_home_hot) {
            @Override
            public void convert(ViewHolder viewHolder, RenterSearchListBean bean) {
                viewHolder.setText(R.id.tv_title, bean.getRoom_name());

                String room_type;
                if (bean.getRoom_type().contains("_")) {
                    String[] t = bean.getRoom_type().split("_");
                    room_type = t[0] + "室" + (t[1].equals("0") ? "" : t[1] + "卫") + t[2] + "厅";
                } else {
                    room_type = bean.getRoom_type();
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(room_type + " | ");
                spannableStringBuilder.append(getArea(bean.getMeasure_area() +""));
                spannableStringBuilder.append(" | " + bean.getFloor() + "/" + bean.getTotal_floor() + "层");
                viewHolder.setText(R.id.tv_date, spannableStringBuilder);

                int text_color = bean.getType() == 1 ? getResources().getColor(R.color.text_green) : getResources().getColor(R.color.text_ren);
                String unit = getString(bean.getType() == 1 ? R.string.public_house_pay_unit_day : R.string.public_house_pay_unit_mon);
                String price = bean.getHousing_price();
                if (!TextUtils.isEmpty(price)) {
                    float priceF = Float.valueOf(bean.getHousing_price());
                    int priceI = (int) priceF;
                    if (priceF <= 0) {
                        price = "";
                        unit = getString(R.string.negotiable);
                    } else if (priceF > priceI) {
                        price = priceF + "";
                    } else {
                        price = priceI + "";
                    }
                } else {
                    price = "";
                }
                viewHolder.setText(R.id.tv_price,price);
                viewHolder.setText(R.id.tv_price_unit, unit);
                viewHolder.setTextColor(R.id.tv_price,text_color);
                viewHolder.setTextColor(R.id.tv_price_unit,text_color);
//                tv_price.setText(Html.fromHtml("<font color='#ff4707'>¥" + roomInfo.housing_price + "</font>/月"));
                if (!TextUtils.isEmpty(bean.getThumb_image()))
                    Picasso.get().load(bean.getThumb_image()).into((ImageView) viewHolder.getView(R.id.iv_icon));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RoomInfoActivity.toActivity(mActivity, bean.getRoom_id());
                    }
                });
            }
        };
        mRvRefresh.setLayoutManager(new LinearLayoutManager(this));
        mRvRefresh.setAdapter(mAdapter);

        mSrlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getHoustListData(true);
            }
        });
        mSrlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getHoustListData(false);
            }
        });
        mSrlRefresh.setEnableLoadmore(false);
        mSrlRefresh.autoRefresh();
    }

    /***************************************************接口*********************************************/

    /**
     * 获取数据
     */
    private void getHoustListData(boolean isRsh) {
        if (isRsh) {
            page = 1;
        } else {
            page++;
        }
        Subscription subscription = SecondRetrofitHelper.getInstance().getRenterRoomList(page + "", "", city == null ? "" : city, "", "", rent_type + "", type + "")
                .compose(RxUtil.<DataInfo<PageDataBean<RenterSearchListBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<PageDataBean<RenterSearchListBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        if (isRsh)
                            mSrlRefresh.finishRefresh();
                        else {
                            page--;
                            mSrlRefresh.finishLoadmore();
                        }
                    }

                    @Override
                    public void onNext(DataInfo<PageDataBean<RenterSearchListBean>> homeInfoDataInfo) {
                        if (isRsh)
                            mSrlRefresh.finishRefresh();
                        else
                            mSrlRefresh.finishLoadmore();
                        if (homeInfoDataInfo.success()) {
                            if (isRsh)
                                mRoomInfos.clear();
                            mRoomInfos.addAll(homeInfoDataInfo.data().getList());
                            mAdapter.notifyDataSetChanged();
                            mSrlRefresh.setEnableLoadmore(homeInfoDataInfo.data().getPage() < homeInfoDataInfo.data().getTotalPage());
                        } else {
                            if (!isRsh) {
                                page--;
                            }
                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
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
