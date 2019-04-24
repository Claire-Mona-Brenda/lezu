package com.konka.renting.tenant.findroom.roominfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.landlord.house.IHouseRefresh;
import com.konka.renting.landlord.house.widget.CircleImageView;
import com.konka.renting.utils.ScreenUtil;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AllCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<RoomInfo.Comment> list;
    Context context;
    MissionitemClick mic;
    IHouseRefresh iHouseRefresh;
    int gray_color = Color.parseColor("#999b9a");

    public AllCommentAdapter(Context context, List<RoomInfo.Comment> list) {
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int arg1) {
        // TODO Auto-generated method stub
        final int index = arg1;
        //if(missionEnity.type==MissionEnity.TYPE_NORMAL){
        MVHolder arg0 = (MVHolder) holder;

        final RoomInfo.Comment h = list.get(index);
        arg0.user_group.removeAllViews();
        int wid = ScreenUtil.dp2Px(context, 100);
        if(h.memberInfo.real_name!=null){
            arg0. name.setText(h.memberInfo.real_name);
        }

        arg0.date.setText(h.time);
        arg0.content.setText(h.content);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wid, wid);
        lp.leftMargin=10;
        for (int i = 0; i < h.image_list.length; i++) {
            ImageView circleImageView = new ImageView(context);
            arg0.  user_group.addView(circleImageView, lp);
            String path = h.image_list[i];
            Picasso.get().load(path).into(circleImageView);
        }
        Picasso.get().load(h.memberInfo.headimgurl).into(arg0.tx);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // TODO Auto-generated method stub

        View v = LayoutInflater.from(context).inflate(
                R.layout.room_comment, arg0, false);
        return new MVHolder(v);
//		}else{
//			View v = LayoutInflater.from(context).inflate(
//					R.layout.lib_missionl_split_adapter, arg0,false);
//			return new SplitMVHolder(v);
//		}

    }

    public class MVHolder extends RecyclerView.ViewHolder {

        TextView name ;
        TextView date;
        TextView content ;
        LinearLayout user_group ;
        CircleImageView tx ;

        public MVHolder(View itemView) {
            super(itemView);
            // TODO Auto-generated constructor stub
             name = (TextView) itemView.findViewById(R.id.name);
             date = (TextView) itemView.findViewById(R.id.date);
             content = (TextView) itemView.findViewById(R.id.content);
             user_group = (LinearLayout) itemView.findViewById(R.id.user_group);
             tx = (CircleImageView) itemView.findViewById(R.id.tx);
        }

    }



    public interface MissionitemClick {
        public void missionItemClick(int position);

        void share(String roomid);
        void openDoor(String gatewayId, String deviceId);
    }

    public void setItemClickListener(MissionitemClick mic) {
        this.mic = mic;
    }
}
