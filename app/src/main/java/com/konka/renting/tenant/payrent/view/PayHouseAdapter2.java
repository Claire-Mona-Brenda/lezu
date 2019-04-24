package com.konka.renting.tenant.payrent.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.landlord.house.PaySeverActivity;
import com.konka.renting.tenant.payrent.PayOrderRequest;
import com.konka.renting.tenant.payrent.PayRentPresent;
import com.konka.renting.tenant.payrent.TenantOrderDetailActivity;
import com.konka.renting.tenant.user.manager.TenantManagerActivity;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PayHouseAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<RenterOrderListBean> list;
    Context context;
    MissionitemClick mic;

    public PayHouseAdapter2(Context context, List<RenterOrderListBean> list) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int arg1) {
        // TODO Auto-generated method stub
        final int index = arg1;
        final RenterOrderListBean missionEnity = list.get(arg1);
        //if(missionEnity.type==MissionEnity.TYPE_NORMAL){
        MVHolder arg0 = (MVHolder) holder;
        arg0.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mic != null) {
                    mic.missionItemClick(index);
                }
            }
        });
        if (!TextUtils.isEmpty(missionEnity.getThumb_image()))
            Picasso.get().load(missionEnity.getThumb_image()).into(arg0.imageView);
        else
            Picasso.get().load(R.mipmap.fangchan_jiazai).into(arg0.imageView);

        arg0.xh.setText("订单编号：" + missionEnity.getOrder_no());
        arg0.status.setText(getStringStatus(missionEnity.getStatus()));
        arg0.adress.setText(missionEnity.getRoom_name());
        int color;
        if (missionEnity.getType() == 1) {
            //短8fc320
            arg0.h_lx.setText("【短租】");
            color = context.getResources().getColor(R.color.color_short);
        } else {
            //长
            arg0.h_lx.setText("【长租】");
            color = context.getResources().getColor(R.color.color_long);
        }
        arg0.h_lx.setTextColor(color);
        arg0.dis_time.setText(missionEnity.getStart_time());
        arg0.end_time.setText(missionEnity.getEnd_time());
        String unit = missionEnity.getType() == 1 ? "/天" : "/月";
        arg0.price.setText("¥" + (int) Float.parseFloat(missionEnity.getHousing_price()) + unit);

        if (missionEnity.getStatus() == 3) {//已入住
            arg0.connect.setVisibility(View.GONE);
            arg0.cancel.setVisibility(View.VISIBLE);
            if (missionEnity.getType() == 1) {
                //短租
                arg0.cancel.setVisibility(View.GONE);
                arg0.fwf.setVisibility(View.GONE);
            } else {
                //长租
                arg0.fwf.setVisibility(View.VISIBLE);
                if (missionEnity.getStatus() == 2) {
                    arg0.renew.setVisibility(View.GONE);
                } else {
                    arg0.renew.setVisibility(View.VISIBLE);
                }
            }
        } else {
            arg0.connect.setVisibility(View.GONE);
            arg0.fwf.setVisibility(View.GONE);
            arg0.cancel.setVisibility(View.GONE);
            arg0.renew.setVisibility(View.GONE);
        }
        arg0.sqtf.setVisibility(View.VISIBLE);


        arg0.cancel.setText(context.getString(R.string.tenant_manager));
        arg0.cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//租客管理
                //new PayOrderRequest(view.getContext()).orderCancel(missionEnity.merge_order_no,"");
                TenantManagerActivity.toActivity(context, missionEnity.getOrder_id());
            }
        });
        arg0.sqtf.setOnClickListener(new OnClickListener() {//申请退房
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle(R.string.renter_apply_to_end).setMessage(R.string.renter_dialog_apply_to_end)
                        .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PayOrderRequest payOrderRequest = new PayOrderRequest(context);
                                payOrderRequest.setRefresh(pRefresh);
                                payOrderRequest.applyCheckout(missionEnity.getOrder_id());
                            }
                        }).setNegativeButton(R.string.warn_cancel, null).create().show();
            }
        });
        arg0.renew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//申请续租
                ApplyReTenantActivity.toActivity(context, missionEnity.getOrder_id(), missionEnity.getMember_phone(), missionEnity.getEnd_time());
            }
        });


        arg0.fwf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//服务续费
                PaySeverActivity.toActivity(context, missionEnity.getRoom_id(), missionEnity.getRoom_name(), missionEnity.getService_date(), 2);
            }
        });
        arg0.mLinOrder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//详情
                TenantOrderDetailActivity.toActivity(context, missionEnity.getOrder_id(), missionEnity.getType() + "");
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // TODO Auto-generated method stub

        View v = LayoutInflater.from(context).inflate(
                R.layout.lib_payhouse_adapter, arg0, false);
        return new MVHolder(v);
//		}else{
//			View v = LayoutInflater.from(context).inflate(
//					R.layout.lib_missionl_split_adapter, arg0,false);
//			return new SplitMVHolder(v);
//		}

    }

    private String getStringStatus(int status) {
        String s = "";
        switch (status) {
            case 1:
                s = context.getString(R.string.order_status_1);
                break;
            case 2:
                s = context.getString(R.string.order_status_2);
                break;
            case 3:
                s = context.getString(R.string.order_status_3);
                break;
            case 4:
                s = context.getString(R.string.order_status_4);
                break;
            case 5:
                s = context.getString(R.string.order_status_5);
                break;
            case 6:
                s = context.getString(R.string.order_status_6);
                break;
            case 7:
                s = context.getString(R.string.order_status_7);
                break;
        }
        return s;
    }


    public class MVHolder extends RecyclerView.ViewHolder {

        TextView xh;
        TextView h_lx;
        TextView status;
        TextView adress;
        TextView price;
        TextView people_num;
        TextView dis_time;
        TextView end_time;
        TextView fwf;
        AppCompatImageView imageView;
        LinearLayout mLinOrder;
        TextView cancel, connect, sqtf, renew;

        public MVHolder(View itemView) {
            super(itemView);
            // TODO Auto-generated constructor stub
            h_lx = (TextView) itemView.findViewById(R.id.h_lx);
            fwf = (TextView) itemView.findViewById(R.id.fwf);
            xh = (TextView) itemView.findViewById(R.id.xh);
            status = (TextView) itemView.findViewById(R.id.status);
            adress = (TextView) itemView.findViewById(R.id.adress);
            price = (TextView) itemView.findViewById(R.id.price);
            status = (TextView) itemView.findViewById(R.id.status);
            dis_time = (TextView) itemView.findViewById(R.id.dis_time);
            end_time = (TextView) itemView.findViewById(R.id.tv_end_time);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.img_house);
            cancel = itemView.findViewById(R.id.cancel);
            connect = itemView.findViewById(R.id.connect);
            sqtf = itemView.findViewById(R.id.sqtf);
            renew = itemView.findViewById(R.id.renew);
            mLinOrder = itemView.findViewById(R.id.lin_order);
        }

    }

    public class SplitMVHolder extends RecyclerView.ViewHolder {
        TextView user;

        public SplitMVHolder(View itemView) {
            super(itemView);
            // TODO Auto-generated constructor stub
            user = itemView.findViewById(R.id.textView1);

        }

    }

    public interface MissionitemClick {
        public void missionItemClick(int position);
    }

    public void setItemClickListener(MissionitemClick mic) {
        this.mic = mic;
    }

    PayRentPresent.PRefresh pRefresh;

    public void setRefresh(PayRentPresent.PRefresh pRefresh) {
        this.pRefresh = pRefresh;
    }
}
