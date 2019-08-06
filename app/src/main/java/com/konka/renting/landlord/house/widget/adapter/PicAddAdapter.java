package com.konka.renting.landlord.house.widget.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.konka.renting.R;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.IPopBack;
import com.konka.renting.landlord.house.widget.LruBitmap;
import com.konka.renting.landlord.house.widget.PopTool;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PicAddAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<MissionEnity> list;
    Context context;
    MissionitemClick mic;
    IPopBack iPopBack;
    int picNumMax = 6;

    public PicAddAdapter(Context context, List<MissionEnity> list) {
        // TODO Auto-generated constructor stub
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return list.size() + 1;
    }

    public void setIPOP(IPopBack ipop) {
        iPopBack = ipop;
    }

    public void setPicNumMax(int picNumMax) {
        this.picNumMax = picNumMax;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int arg1) {
        // TODO Auto-generated method stub
        final int index = arg1;
        MVHolder arg0 = (MVHolder) holder;
        if (index == list.size()) {
            arg0.add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (list.size() < picNumMax)
                        addImg();
                    else
                        ShowToastUtil.showNormalToast(context, "添加图片数量不能超过" + picNumMax + "张");
                }
            });
            arg0.add.setVisibility(View.VISIBLE);
            arg0.content_img.setVisibility(View.GONE);
            arg0.del.setVisibility(View.GONE);
        } else {
            MissionEnity missionEnity = list.get(arg1);
            //if(missionEnity.type==MissionEnity.TYPE_NORMAL){
            arg0.itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (mic != null) {
                        mic.missionItemClick(index);
                    }
                }
            });
            arg0.add.setVisibility(View.GONE);
            arg0.del.setVisibility(View.VISIBLE);
            arg0.content_img.setVisibility(View.VISIBLE);
            if (missionEnity.isNet) {
                if (missionEnity.imgpath != null && !missionEnity.imgpath.equals(""))
                    Picasso.get().load(missionEnity.imgpath).placeholder(R.mipmap.fangchan_jiazai).into(arg0.content_img);
                else
                    Picasso.get().load(R.mipmap.fangchan_jiazai).placeholder(R.mipmap.fangchan_jiazai).into(arg0.content_img);
            } else {
                Bitmap bm = LruBitmap.getBitmap(missionEnity.imgpath);
                if (bm == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bm = BitmapFactory.decodeFile(missionEnity.imgpath, options);
                    LruBitmap.addBitmap(missionEnity.imgpath, bm);
                }
                arg0.content_img.setImageBitmap(bm);
            }

        }

        arg0.del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iPopBack != null)
                    iPopBack.delCallBank(index);
                list.remove(index);
                PicAddAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // TODO Auto-generated method stub

        View v = LayoutInflater.from(context).inflate(
                R.layout.lib_picadd_adapter, arg0, false);
        return new MVHolder(v);


    }

    public class MVHolder extends RecyclerView.ViewHolder {


        ImageView add;
        ImageView del;
        ImageView content_img;

        public MVHolder(View itemView) {
            super(itemView);
            // TODO Auto-generated constructor stub

            add = (ImageView) itemView.findViewById(R.id.content_add);
            content_img = (ImageView) itemView.findViewById(R.id.content_img);
            del = (ImageView) itemView.findViewById(R.id.del);
        }

    }


    public interface MissionitemClick {
        public void missionItemClick(int position);
    }

    public void setItemClickListener(MissionitemClick mic) {
        this.mic = mic;
    }

    public void addImg() {
        String[] s2 = {"相机", "图库"};
        new PopTool().showPopupWindow(context, s2, iPopBack);
    }

}
