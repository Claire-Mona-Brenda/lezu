package com.konka.renting.guide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.utils.UIUtils;
import com.squareup.picasso.Picasso;

/**
 * @author Nate Robinson
 * @Time 2017/12/24
 */

public class GuideFragment extends BaseFragment {

    public static GuideFragment newInstance(int index) {
        GuideFragment fragment = new GuideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }

    //    private int[] guides = new int[]{R.mipmap.guide_one, R.mipmap.guide_two, R.mipmap.guide_three};
    private int[] guides = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    //private int[] guidetitles = new int[]{R.string.guide_one_title_txt, R.string.guide_two_title_txt, R.string.guide_three_title_txt};
    private ImageView guideItemIv;
    private TextView guide_tv;
    private View point_one;
    private View point_two;
    private View point_three;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        guideItemIv = (ImageView) view.findViewById(R.id.guide_iv);
        init();
        return view;
    }

    @Override
    public void init() {
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt("index");
        }
        //从res资源文件中加载图片
        Picasso.get().load(guides[index])
                .resize(UIUtils.getScreenWidth(), UIUtils.getScreenHeight())
                .centerInside()
                .into(guideItemIv);
    }

}
