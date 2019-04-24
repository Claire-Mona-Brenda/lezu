package com.konka.renting.landlord.house.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.konka.renting.R;
import com.konka.renting.landlord.house.view.AutoChooseWidget;

import java.util.HashMap;


/**
 * Created by jbl on 2018/3/10.
 */

public class HouseCommonWidget extends LinearLayout implements AutoChooseWidget.IForAdd ,AutoChooseWidget.ItemSelect2{
    LinearLayout last_add;
    HashMap<String,String>tag_added;
    public HouseCommonWidget(Context context) {
        super(context);
        addTopView();
    }

    public HouseCommonWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addTopView();
    }

    public HouseCommonWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTopView();
    }

    public void addTopView() {
        tag_added=new HashMap<>();
        this.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout relativeLayout = new RelativeLayout(this.getContext());
        TextView textView = new TextView(this.getContext());
        textView.setText("公共配置");
        textView.setTextSize(13);
        textView.setTextColor(Color.parseColor("#000000"));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(textView, lp);
        TextView button = new TextView(this.getContext());
        button.setTextColor(getResources().getColor(R.color.lib_landlord_house));
        button.setText("添加");
        button.setPadding(15,10,15,10);
        button.setBackgroundResource(R.drawable.shape_house);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp2.addRule(RelativeLayout.CENTER_VERTICAL);
        lp2.topMargin = 10;
        lp2.bottomMargin = 10;
//        button.setPadding(0,20,0,10);
        relativeLayout.addView(button, lp2);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addCommonConfig("", true);
            }
        });
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(relativeLayout, layoutParams);


    }

    public void setConfig(String[] con) {
        for (String name : con) {
            tag_added.put(name,name);
            addCommonConfig(name, false);
        }
    }
     LinearLayout linearLayout;
    public void addCommonConfig(String name, boolean pop) {

        LinearLayout.LayoutParams lp_line=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
          linearLayout=new LinearLayout(this.getContext());
        linearLayout.setOrientation(VERTICAL);
        final TextView line = new TextView(this.getContext());
        line.setBackgroundColor(Color.parseColor("#F0F0F0"));
        line.setHeight(2);
        linearLayout.addView(line);
        final RelativeLayout relativeLayout = new RelativeLayout(this.getContext());
        last_add=linearLayout;
        TextView textView = new TextView(this.getContext());
        textView.setText(name);
        textView.setTextSize(13);
        textView.setPadding(5,15,0,15);
        textView.setTextColor(Color.parseColor("#000000"));
//        textView.setTag("config");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(textView, lp);

        ImageView imageView = new ImageView(this.getContext());
        imageView.setImageResource(R.mipmap.right);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.addView(imageView, lp2);

        ImageView imageView2 = new ImageView(this.getContext());
        imageView2.setImageResource(R.mipmap.lib_del);
        imageView2.setPadding(5,5,5,5);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp3.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeLayout.addView(imageView2, lp3);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 12;
        linearLayout.addView(relativeLayout,lp_line);
        addView(linearLayout, layoutParams);
        if (pop) {
            addConfig(textView);
        }

        imageView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HouseCommonWidget.this.removeView(linearLayout);
            }
        });
    }

    public void addConfig(TextView textView) {
        AutoChooseWidget autoChooseWidget=new AutoChooseWidget(this.getContext(), AutoChooseWidget.ROOM_CONFIG, textView);
        autoChooseWidget.setIfor(this);
        autoChooseWidget.setItemSelect2(this);
        autoChooseWidget .showPopWindow();
    }

    public String getAllConfigID() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i < this.getChildCount(); i++) {
            LinearLayout linearLayout= (LinearLayout) this.getChildAt(i);
            RelativeLayout relativeLayout = (RelativeLayout)linearLayout .getChildAt(1);
            stringBuffer.append(relativeLayout.getChildAt(0).getTag().toString());
            stringBuffer.append(",");
        }
        String res = stringBuffer.toString();
        if (!TextUtils.isEmpty(res)) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    public String getAllConfig() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i < this.getChildCount(); i++) {
            LinearLayout linearLayout= (LinearLayout) this.getChildAt(i);
            RelativeLayout relativeLayout = (RelativeLayout)linearLayout .getChildAt(1);
            TextView textView = (TextView) relativeLayout.getChildAt(0);
            stringBuffer.append(textView.getText());
            stringBuffer.append(",");
        }
        String res = stringBuffer.toString();
        if (!TextUtils.isEmpty(res)) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    @Override
    public void remove() {
        removeView(last_add);
    }

    @Override
    public void itemSelect2(String type) {
        if(tag_added.containsKey(type)){
            HouseCommonWidget.this.removeView(linearLayout);
            return;
        }
        if(!TextUtils.isEmpty(type)){
            tag_added.put(type,type);
        }
    }
}
