package com.konka.renting.landlord.house.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.adapter.PicstandardAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ATI on 2017/9/6.
 */
public class PicstandardWidget extends LinearLayout implements View.OnClickListener {
    LinearLayout content;
    Button num;
    TextView line;
    ImageView add;
    int img_width = 256, img_height = 170;
    FrameLayout hor;
    RecyclerView recyclerview;
    PicstandardAdapter picAddAdapter;
    IPopBack ipop;
    List<MissionEnity> list;
    OnItemClickCall itemClickCall;

    public PicstandardWidget(Context context) {
        super(context);
        init();
    }

    public PicstandardWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setIPOP(IPopBack ipop2) {
        this.ipop = ipop2;
        picAddAdapter.setIPOP(ipop);
    }

    public void init() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.lib_picrecord_widget, this, true);
        list = new ArrayList<>();

        picAddAdapter = new PicstandardAdapter(this.getContext(), list);
        picAddAdapter.setItemClickListener(new PicstandardAdapter.MissionitemClick() {
            @Override
            public void missionItemClick(int position) {
                if (itemClickCall!=null){
                    itemClickCall.onClick(position);
                }
            }
        });

        recyclerview = (RecyclerView) findViewById(R.id.content);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerview.setLayoutManager(lm);
        recyclerview.setAdapter(picAddAdapter);
    }

    public List<MissionEnity> getImgs() {
        return list;
    }

    public void setItemClickCall(OnItemClickCall itemClickCall) {
        this.itemClickCall = itemClickCall;
    }

    @Override
    public void onClick(View v) {


    }

    public void addImg(MissionEnity missionEnity) {
        list.add(missionEnity);
        picAddAdapter.notifyDataSetChanged();
    }
    public interface OnItemClickCall{
        void onClick(int position);
    }
}
