package com.konka.renting.landlord.home.tenanter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.base.BaseDialog;
import com.konka.renting.bean.RoomInfo;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzxiang on 17/03/2018.
 */

public class TenanterDesDialog extends BaseDialog {

    View mRootView;
    private ViewHolder mViewHolder;

    public TenanterDesDialog(Context context) {
        super(context);
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.common_dialog_style;
    }

    @Override
    protected View getView() {
        if (mRootView == null) {
            mRootView = LayoutInflater.from(context).inflate(R.layout.landlord_tenanter_dialog, null);
            mViewHolder = new ViewHolder(mRootView);
            mViewHolder.mIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
        return mRootView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_close)
        ImageView mIvClose;
        @BindView(R.id.iv_photo)
        ImageView mIvPhoto;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_gender)
        TextView mTvGender;
        @BindView(R.id.tv_phone)
        TextView mTvPhone;
        @BindView(R.id.btn_call_phone)
        Button mBtnCall;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public void show(final RoomInfo.Member member) {
        Picasso.get().load(member.headimgurl).into(mViewHolder.mIvPhoto);
        mViewHolder.mTvName.setText(member.real_name);
        mViewHolder.mTvGender.setText(member.getGender());
        mViewHolder.mTvPhone.setText("");
        mViewHolder.mBtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(member.tel);
            }
        });
        show();
    }


    private void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
