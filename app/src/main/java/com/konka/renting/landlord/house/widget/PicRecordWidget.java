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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.adapter.PicAddAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ATI on 2017/9/6.
 */
public class PicRecordWidget extends LinearLayout implements View.OnClickListener {
    LinearLayout content;
    Button num;
    TextView line;
    ImageView add;
    int img_width = 256, img_height = 170;
    FrameLayout hor;
    RecyclerView recyclerview;
    PicAddAdapter picAddAdapter;
    IPopBack ipop;
    List<MissionEnity> list;

    public PicRecordWidget(Context context) {
        super(context);
        init();
    }

    public PicRecordWidget(Context context, AttributeSet attrs) {
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

        picAddAdapter = new PicAddAdapter(this.getContext(), list);

        recyclerview = (RecyclerView) findViewById(R.id.content);
        GridLayoutManager lm = new GridLayoutManager(getContext(), 3);

        recyclerview.setLayoutManager(lm);
        recyclerview.setAdapter(picAddAdapter);
    }

    public List<MissionEnity> getImgs() {
        return list;
    }

    @Override
    public void onClick(View v) {


    }

    public void addImg(MissionEnity missionEnity) {
        list.add(missionEnity);
        picAddAdapter.notifyDataSetChanged();
    }
    public void setPicNumMax(int picNumMax) {
        picAddAdapter.setPicNumMax(picNumMax);
    }
}
