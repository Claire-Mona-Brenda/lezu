package com.konka.renting.landlord.house.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.HouseOrderInfoBean;
import com.konka.renting.landlord.house.CreateOrderActivity;
import com.konka.renting.landlord.house.IHouseRefresh;
import com.konka.renting.landlord.house.PaySeverActivity;
import com.konka.renting.landlord.house.activity.DevListActivity;
import com.konka.renting.landlord.house.activity.PayAllMoneyActivity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.CacheUtils;
import com.konka.renting.widget.CommonPopupWindow;
import com.squareup.picasso.Picasso;

import java.util.List;


public class HouseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<HouseOrderInfoBean> list;
    Context context;
    MissionitemClick mic;
    IHouseRefresh iHouseRefresh;
    CommonPopupWindow commonPopupWindow;

    public HouseAdapter(Context context, List<HouseOrderInfoBean> list) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    public void setiHouseRefresh(IHouseRefresh iHouseRefresh) {
        this.iHouseRefresh = iHouseRefresh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO Auto-generated method stub
        final int index = position;
        final HouseOrderInfoBean houseOrderInfoBean = list.get(position);
        MVHolder nvHolder = (MVHolder) holder;
        nvHolder.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mic != null) {
                    mic.missionItemClick(index);
                }
            }
        });
        nvHolder.tvId.setText(houseOrderInfoBean.getRoom_no());
        nvHolder.tvAdress.setText(houseOrderInfoBean.getRoom_name());
        nvHolder.tvSeverEndTime.setText(houseOrderInfoBean.getService_date().equals("0") ? (houseOrderInfoBean.getRoom_status() < 3 ? context.getString(R.string.house_sever_end_time_emty) : context.getString(R.string.house_sever_end_time_end)) : houseOrderInfoBean.getService_date());
        if (CacheUtils.checkFileExist(houseOrderInfoBean.getThumb_image())) {
            Picasso.get().load(CacheUtils.getFile(houseOrderInfoBean.getThumb_image())).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(nvHolder.imageView);
        } else if (!TextUtils.isEmpty(houseOrderInfoBean.getThumb_image())) {
            CacheUtils.saveFile(houseOrderInfoBean.getThumb_image(), context);
            Picasso.get().load(houseOrderInfoBean.getThumb_image()).placeholder(R.mipmap.fangchan_jiazai).error(R.mipmap.fangchan_jiazai).into(nvHolder.imageView);
        } else
            Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai).into(nvHolder.imageView);
        nvHolder.tvRentType.setVisibility(View.GONE);
        switch (houseOrderInfoBean.getRoom_status()) {
            case 0:
            case 1://未缴纳安装费
                nvHolder.tvStatus.setText(houseOrderInfoBean.getIs_del() == 1 ? R.string.house_status_type_1 : R.string.house_status_type_7);
            case 2://待安装认证
                nvHolder.tvStatus.setText(houseOrderInfoBean.getIs_del() == 1 ? R.string.house_status_type_2 : R.string.house_status_type_7);
            case 3://待发布
                nvHolder.tvStatus.setText(houseOrderInfoBean.getIs_del() == 1 ? R.string.house_status_type_3 : R.string.house_status_type_7);
                nvHolder.tvStartEnd.setText(R.string.start_to_rent);
                nvHolder.llStartEnd.setVisibility(houseOrderInfoBean.getIs_del() == 1 ? View.VISIBLE : View.GONE);
                nvHolder.llCreateOrder.setVisibility(View.GONE);
                if (TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.VISIBLE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                } else if (houseOrderInfoBean.getIs_install() == 0) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.VISIBLE);
                } else {
                    nvHolder.llPaySever.setVisibility(View.VISIBLE);
                    nvHolder.llOpen.setVisibility(View.VISIBLE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                }

                break;
            case 4://已发布,等待被出租
                nvHolder.tvRentType.setVisibility(View.VISIBLE);
                nvHolder.llStartEnd.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.VISIBLE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                } else if (houseOrderInfoBean.getIs_install() == 0) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.VISIBLE);
                } else {
                    nvHolder.llPaySever.setVisibility(View.VISIBLE);
                    nvHolder.llOpen.setVisibility(View.VISIBLE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                }

                nvHolder.tvStartEnd.setText(R.string.end_to_rent);
                nvHolder.tvStatus.setText(R.string.house_status_type_4);
                switch (houseOrderInfoBean.getType()) {
                    case 1://短租
                        nvHolder.llCreateOrder.setVisibility(View.VISIBLE);
                        nvHolder.tvRentType.setText("【" + context.getString(R.string.short_rent) + "】");
                        nvHolder.tvRentType.setTextColor(context.getResources().getColor(R.color.color_short));
                        break;
                    case 2://长租
                        nvHolder.llCreateOrder.setVisibility(View.GONE);
                        nvHolder.tvRentType.setText("【" + context.getString(R.string.long_rent) + "】");
                        nvHolder.tvRentType.setTextColor(context.getResources().getColor(R.color.color_long));
                        break;
                }
                break;
            case 5://已确定
                nvHolder.tvRentType.setVisibility(View.VISIBLE);
                nvHolder.llStartEnd.setVisibility(View.GONE);
                if (TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.VISIBLE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                } else if (houseOrderInfoBean.getIs_install() == 0) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.VISIBLE);
                } else {
                    nvHolder.llPaySever.setVisibility(View.VISIBLE);
                    nvHolder.llOpen.setVisibility(View.VISIBLE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                }

                nvHolder.tvStatus.setText(R.string.house_status_type_5);
                switch (houseOrderInfoBean.getType()) {
                    case 1://短租
                        nvHolder.llCreateOrder.setVisibility(View.VISIBLE);
                        nvHolder.tvRentType.setText("【" + context.getString(R.string.short_rent) + "】");
                        nvHolder.tvRentType.setTextColor(context.getResources().getColor(R.color.color_short));
                        break;
                    case 2://长租
                        nvHolder.llCreateOrder.setVisibility(View.GONE);
                        nvHolder.tvRentType.setText("【" + context.getString(R.string.long_rent) + "】");
                        nvHolder.tvRentType.setTextColor(context.getResources().getColor(R.color.color_long));
                        break;
                }
                break;
            case 6://已出租
                nvHolder.tvRentType.setVisibility(View.VISIBLE);
                nvHolder.llStartEnd.setVisibility(View.GONE);
                if (TextUtils.isEmpty(houseOrderInfoBean.getDevice_id())) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.VISIBLE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                } else if (houseOrderInfoBean.getIs_install() == 0) {
                    nvHolder.llPaySever.setVisibility(View.GONE);
                    nvHolder.llOpen.setVisibility(View.GONE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.VISIBLE);
                } else {
                    nvHolder.llPaySever.setVisibility(View.VISIBLE);
                    nvHolder.llOpen.setVisibility(View.VISIBLE);
                    nvHolder.llBind.setVisibility(View.GONE);
                    nvHolder.llInstall.setVisibility(View.GONE);
                }

                nvHolder.tvStatus.setText(R.string.house_status_type_6);
                switch (houseOrderInfoBean.getType()) {
                    case 1://短租
                        nvHolder.llCreateOrder.setVisibility(View.VISIBLE);
                        nvHolder.tvRentType.setText("【" + context.getString(R.string.short_rent) + "】");
                        nvHolder.tvRentType.setTextColor(context.getResources().getColor(R.color.color_short));
                        break;
                    case 2://长租
                        nvHolder.llCreateOrder.setVisibility(View.GONE);
                        nvHolder.tvRentType.setText("【" + context.getString(R.string.long_rent) + "】");
                        nvHolder.tvRentType.setTextColor(context.getResources().getColor(R.color.color_long));
                        break;
                }
                break;
        }
        nvHolder.llStartEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//发布房产或取消发布

                switch (houseOrderInfoBean.getRoom_status()) {
                    case 1:
                    case 2:
                    case 3://待发布
                        if (houseOrderInfoBean.getIs_del() == 1)
                            HousePublishActivity.toActivity(context, houseOrderInfoBean.getRoom_id() + "");
                        else
                            ShowToastUtil.showNormalToast(context, context.getString(R.string.warm_del_context_no));
                        break;
                    case 4://待租出
                        new AlertDialog.Builder(context).setTitle(R.string.end_to_rent).setMessage("确定执行此操作！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mic.cancelPublic(houseOrderInfoBean.getRoom_id() + "");
                            }
                        }).setNegativeButton("取消", null).show();
                        break;
                }
            }
        });
        nvHolder.llCreateOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//短租生成订单
                CreateOrderActivity.toActivity(context, houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getRoom_name());
            }
        });

        nvHolder.llOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//开门
                if (!houseOrderInfoBean.getDevice_id().equals("")) {
                    showOpen(houseOrderInfoBean);
                } else {
                    ShowToastUtil.showNormalToast(context, context.getString(R.string.warm_open_no_device));
                }


            }
        });
        nvHolder.llBind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//绑定设备
                DevListActivity.toActivity(context, houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getRoom_status(), houseOrderInfoBean.getIs_install() == 0, false);
            }
        });
        nvHolder.llInstall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//安装缴费
                PayAllMoneyActivity.toActivity(context, houseOrderInfoBean.getRoom_id() + "");
            }
        });

        nvHolder.llPaySever.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//缴纳服务费
//                PayAllMoneyActivity.toActivity(context, houseOrderInfoBean.getRoom_id() + "");
                PaySeverActivity.toActivity(context, houseOrderInfoBean.getRoom_id() + "", houseOrderInfoBean.getAddress(), houseOrderInfoBean.getService_date(), 1);
            }
        });

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View v = LayoutInflater.from(context).inflate(
                R.layout.lib_house_adapter, arg0, false);
        return new MVHolder(v);
    }

    public class MVHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvStatus;
        TextView tvRentType;
        TextView tvAdress;
        TextView tvSeverEndTime;
        AppCompatImageView imageView;
        LinearLayout llAction;
        TextView tvStartEnd;
        TextView tvOpen;
        TextView tvBind;
        TextView tvInstall;
        TextView tvPaySever;
        TextView tvCreateOrder;
        LinearLayout llStartEnd;
        LinearLayout llOpen;
        LinearLayout llBind;
        LinearLayout llInstall;
        LinearLayout llPaySever;
        LinearLayout llCreateOrder;

        public MVHolder(View itemView) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.adapter_houselist_tv_id);
            tvStatus = (TextView) itemView.findViewById(R.id.status);
            tvRentType = itemView.findViewById(R.id.adapter_houselist_tv_rent_type);
            tvAdress = (TextView) itemView.findViewById(R.id.adress);
            tvSeverEndTime = (TextView) itemView.findViewById(R.id.adapter_houselist_tv_endtime);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.img_house);
            llAction = itemView.findViewById(R.id.adapter_houselist_ll_action);
            llStartEnd = itemView.findViewById(R.id.ll_start_end);
            tvStartEnd = itemView.findViewById(R.id.start_end);
            llOpen = itemView.findViewById(R.id.ll_open);
            tvOpen = itemView.findViewById(R.id.tv_open);
            llBind = itemView.findViewById(R.id.ll_bind);
            tvBind = itemView.findViewById(R.id.tv_bind);
            llInstall = itemView.findViewById(R.id.ll_install);
            tvInstall = itemView.findViewById(R.id.tv_install);
            llPaySever = itemView.findViewById(R.id.adapter_houselist_ll_sever);
            tvPaySever = itemView.findViewById(R.id.adapter_houselist_tv_sever);
            llCreateOrder = itemView.findViewById(R.id.ll_create_order);
            tvCreateOrder = itemView.findViewById(R.id.create_order);
        }

    }

    private void showOpen(final HouseOrderInfoBean bean) {
        commonPopupWindow = new CommonPopupWindow.Builder(context)
                .setTitle(context.getString(R.string.tips))
                .setContent(context.getString(R.string.warm_open_door))
                .setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mic.openDoor(bean.getRoom_id() + "", bean.getGateway_id(), bean.getDevice_id());
                        commonPopupWindow.setOnDismissListener(null);
                        commonPopupWindow.dismiss();
                    }
                })
                .create();
        showPopup(commonPopupWindow);
    }

    private void showPopup(PopupWindow popupWindow) {
        // 开启 popup 时界面透明
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = 0.5f;
        ((Activity) context).getWindow().setAttributes(lp);
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // popupwindow 第一个参数指定popup 显示页面
        popupWindow.showAtLocation((View) ((Activity) context).findViewById(R.id.title_bg).getParent(), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, -200);     // 第一个参数popup显示activity页面
        // popup 退出时界面恢复
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                ((Activity) context).getWindow().setAttributes(lp);
            }
        });
    }

    public interface MissionitemClick {
        public void missionItemClick(int position);

        void share(String roomid);

        void openDoor(String room_id, String gatewayId, String deviceId);

        void cancelPublic(String roomid);
    }

    public void setItemClickListener(MissionitemClick mic) {
        this.mic = mic;
    }
}
