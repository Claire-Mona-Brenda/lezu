package com.konka.renting.landlord.house.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.event.AddHouseCompleteEvent;
import com.konka.renting.http.SecondRetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.widget.CheckHouseConfigPopup;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.utils.RxUtil;
import com.konka.renting.widget.CommonPopupWindow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.listeners.OnMoreWheelListener;
import cn.addapp.pickers.picker.LinkagePicker;
import rx.Subscription;
import rx.functions.Action1;

public class AddHouseInfoActivity extends BaseActivity {
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
    @BindView(R.id.activity_add_house_info_edt_name)
    EditText mEdtName;
    @BindView(R.id.activity_add_house_info_ll_name)
    LinearLayout mLlName;
    @BindView(R.id.activity_add_house_info_tv_house_type)
    TextView mTvHouseType;
    @BindView(R.id.activity_add_house_info_ll_house_type)
    LinearLayout mLlHouseType;
    @BindView(R.id.activity_add_house_info_edt_area)
    EditText mEdtArea;
    @BindView(R.id.activity_add_house_info_ll_area)
    LinearLayout mLlArea;
    @BindView(R.id.activity_add_house_info_edt_floor)
    EditText mEdtFloor;
    @BindView(R.id.activity_add_house_info_edt_floor_sum)
    EditText mEdtFloorSum;
    @BindView(R.id.activity_add_house_info_ll_floor)
    LinearLayout mLlFloor;
    @BindView(R.id.activity_add_house_info_tv_confit)
    TextView mTvConfit;
    @BindView(R.id.activity_add_house_info_rl_confit)
    RelativeLayout mRlConfit;

    PoiItem mPoiItem;
    String address;
    List<HouseConfigBean> configList;
    CheckHouseConfigPopup mConfigPopup;
    ArrayList<String> firstList = new ArrayList<>();
    ArrayList<String> secondList = new ArrayList<>();
    ArrayList<String> thirdList = new ArrayList<>();
    int mFirst;
    int mSecond;
    int mThird;

    public static void toActivity(Context context, PoiItem mPoiItem, String address) {
        Intent intent = new Intent(context, AddHouseInfoActivity.class);
        intent.putExtra(PoiItem.class.getSimpleName(), mPoiItem);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_house_info;
    }

    @Override
    public void init() {
        mPoiItem = getIntent().getParcelableExtra(PoiItem.class.getSimpleName());
        address = getIntent().getStringExtra("address");

        tvTitle.setText(R.string.add_house);
        tvTitle.setTypeface(Typeface.SANS_SERIF);
        TextPaint paint = tvTitle.getPaint();
        paint.setFakeBoldText(true);
        tvRight.setText(R.string.common_next);
        tvRight.setTextColor(getResources().getColor(R.color.text_blue));
        tvRight.setVisibility(View.VISIBLE);

        configList = new ArrayList<>();

        addRxBusSubscribe(AddHouseCompleteEvent.class, new Action1<AddHouseCompleteEvent>() {
            @Override
            public void call(AddHouseCompleteEvent addHouseCompleteEvent) {
                finish();
            }
        });

        initPicker();
        initConfig();
        initListent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (configList.size() <= 0)
            getHouseConfig();
    }

    private void initPicker() {
        for (int i = 0; i < 10; i++) {
            firstList.add(i + " 室");
            secondList.add(i + " 卫");
            thirdList.add(i + " 厅");
        }
    }

    private void initListent() {

        mEdtArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String area = mEdtArea.getText().toString();
                if (area.startsWith(".")) {
                    area = area.replace(".", "");
                    mEdtArea.setText(area);
                    mEdtArea.setSelection(area.length());
                } else if (area.startsWith("0") && area.length() > 1 && !area.startsWith("0.")) {
                    mEdtArea.setText("0");
                    mEdtArea.setSelection(1);
                } else if (area.contains(".")) {
                    int index = area.indexOf(".");
                    if (index < area.length() - 1) {
                        String a = area.substring(index + 1, area.length());
                        if (a.contains(".")) {
                            area = area.substring(0, index) + "." + a.replace(".", "");
                            mEdtArea.setText(area);
                            mEdtArea.setSelection(area.length());
                        } else if (index == 5) {
                            area = area.substring(0, index);
                            mEdtArea.setText(area);
                            mEdtArea.setSelection(area.length());
                        }

                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEdtFloor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String floor = mEdtFloor.getText().toString();
                if (floor.length() > 1) {
                    String str = floor.substring(1, floor.length());
                    if (str.contains("-") || str.contains("b") || str.contains("B")) {
                        floor = floor.charAt(0) + str.replace("-", "").replace("b", "").replace("B", "");
                        mEdtFloor.setText(floor);
                        mEdtFloor.setSelection(floor.length());
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.activity_add_house_info_ll_house_type, R.id.activity_add_house_info_rl_confit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                next();
                break;
            case R.id.activity_add_house_info_ll_house_type:
                onLinkagePicker();
                break;
            case R.id.activity_add_house_info_rl_confit:
                showConfigPop();
                break;
        }
    }

    private void next() {
        String name = mEdtName.getText().toString();
        String houseType = mTvHouseType.getText().toString();
        String area = mEdtArea.getText().toString();
        String floor = mEdtFloor.getText().toString();
        String floorSum = mEdtFloorSum.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showToast(R.string.add_house_edit_name);
            return;
        }
        if (TextUtils.isEmpty(houseType)) {
            showToast(R.string.add_house_edit_type);
            return;
        }
        if (TextUtils.isEmpty(area)) {
            showToast(R.string.add_house_edit_area);
            return;
        }
        if (TextUtils.isEmpty(floor.replace("-", "").replace("B", "").replace("b", ""))) {
            showToast(R.string.add_house_edit_floor);
            return;
        }
        if (TextUtils.isEmpty(floorSum)) {
            showToast(R.string.add_house_edit_floor_sum);
            return;
        }

        int fl = getFloor(mEdtFloor.getText().toString());
        int flS = Integer.valueOf(mEdtFloorSum.getText().toString());
        if (fl == 0) {
            ShowToastUtil.showNormalToast(this, getString(R.string.warm_house_floor_0));
            return;
        } else if (flS == 0) {
            ShowToastUtil.showNormalToast(this, getString(R.string.warm_house_floor_sum_0));
            return;
        } else if (fl > flS) {
            ShowToastUtil.showNormalToast(this, getString(R.string.warm_house_floor_sum));
            return;
        }
        String room_type = mFirst + "_" + mSecond + "_" + mThird;
        String config = "";
        int len = configList.size();
        for (int i = 0; i < len; i++) {
            HouseConfigBean bean = configList.get(i);
            if (bean.getStatus() == 1) {
                if (!config.equals(""))
                    config += ",";
                config += bean.getId();
            }

        }
        AddHouseIntroduceActivity.toActivity(this, mPoiItem, address, name, room_type, config, floorSum, floor, area);
    }

    private int getFloor(String floor) {
        int f;
        if (floor.startsWith("B") || floor.startsWith("b") || floor.startsWith("-")) {
            f = -Integer.valueOf(floor.replace("B", "").replace("b", "").replace("-", ""));
        } else if (floor.contains("B") || floor.contains("b") || floor.contains("-")) {
            f = 0;
        } else {
            f = Integer.valueOf(floor);
        }
        return f;
    }

    public void onLinkagePicker() {
        LinkagePicker.DataProvider provider = new LinkagePicker.DataProvider() {

            @Override
            public boolean isOnlyTwo() {
                return false;
            }

            @Override
            public List<String> provideFirstData() {
                return firstList;
            }

            @Override
            public List<String> provideSecondData(int firstIndex) {
                return secondList;
            }

            @Override
            public List<String> provideThirdData(int firstIndex, int secondIndex) {
                return thirdList;
            }

        };
        LinkagePicker picker = new LinkagePicker(this, provider);
        picker.setCanLoop(false);
        picker.setCanLinkage(false);
        picker.setOnMoreItemPickListener(new OnMoreItemPickListener<String>() {

            @Override
            public void onItemPicked(String first, String second, String third) {
                mFirst = Integer.valueOf(first.charAt(0)+"");
                mSecond = Integer.valueOf(second.charAt(0)+"");
                mThird = Integer.valueOf(third.charAt(0)+"");
                mTvHouseType.setText(first + " " + second + " " + third);
            }
        });
        picker.setOnMoreWheelListener(new OnMoreWheelListener() {
            @Override
            public void onFirstWheeled(int index, String item) {

            }

            @Override
            public void onSecondWheeled(int index, String item) {

            }

            @Override
            public void onThirdWheeled(int index, String item) {

            }
        });
        picker.show();
    }

    private void initConfig() {
        mConfigPopup = new CheckHouseConfigPopup(this);
        mConfigPopup.setmCallBack(new CheckHouseConfigPopup.OnCallBack() {
            @Override
            public void onRefresh() {
                getHouseConfig();
            }

            @Override
            public void sure(ArrayList<HouseConfigBean> list) {
                configList.clear();
                configList.addAll(list);
                String str = "";
                int size = configList.size();
                for (int i = 0; i < size; i++) {
                    if (configList.get(i).getStatus() == 1) {
                        str += configList.get(i).getName() + "、";
                    }
                }
                if (str.length() > 1) {
                    str = str.substring(0, str.length() - 1);
                }
                mTvConfit.setText(str);
            }
        });
    }

    /***************************************************接口*************************************************/
    private void getHouseConfig() {
        Subscription subscription = SecondRetrofitHelper.getInstance().getRoomConfigList()
                .compose(RxUtil.<DataInfo<List<HouseConfigBean>>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<List<HouseConfigBean>>>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<List<HouseConfigBean>> dataInfo) {
                        if (dataInfo.success()) {
                            if (configList.size() <= 0) {
                                configList.addAll(dataInfo.data());
                            }
                            if (mConfigPopup != null) {
                                mConfigPopup.onRefresh(dataInfo.data());
                            }
                        }

                    }
                });
        addSubscrebe(subscription);
    }

    /******************************************************弹窗**********************************************/
    private void showConfigPop() {
        if (mConfigPopup == null) {
            initConfig();
        }
        mConfigPopup.setData(configList);
        showPopup(mConfigPopup);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) findViewById(R.id.lin_title).getParent(), Gravity.BOTTOM, 0, 0);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });
    }
}
