package com.konka.renting.landlord.house;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PicViewPagerActivity extends BaseActivity {
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
    @BindView(R.id.activity_pic_viewpager_viewpager)
    ViewPager viewpager;

    List<String> auth_image;

    public static void toActivity(Context context, ArrayList<String> auth_image, int position) {
        Intent intent = new Intent(context, PicViewPagerActivity.class);
        intent.putStringArrayListExtra("auth_image", auth_image);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_pic_viewpager;
    }

    @Override
    public void init() {
        tvTitle.setText("0/0");
        auth_image = getIntent().getStringArrayListExtra("auth_image");
        if (auth_image == null)
            return;
        int position = getIntent().getIntExtra("position", 0);
        viewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return auth_image == null ? 0 : auth_image.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = View.inflate(getBaseContext(), R.layout.adapter_viewpager_pic, null);
                ImageView img = view.findViewById(R.id.adapter_pic_viewpage_img);
                Picasso.get().load(auth_image.get(position)).placeholder(R.mipmap.fangchan_jiazai).into(img);
                container.addView(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
        viewpager.setCurrentItem(position);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tvTitle.setText(i + 1 + "/" + (auth_image == null ? 0 : auth_image.size()));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tvTitle.setText(position + 1 + "/" + (auth_image == null ? 0 : auth_image.size()));

    }



    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
