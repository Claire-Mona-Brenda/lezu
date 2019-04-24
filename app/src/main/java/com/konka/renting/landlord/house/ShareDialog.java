package com.konka.renting.landlord.house;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.konka.renting.R;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by ATI on 2018/5/6.
 */

public class ShareDialog {
    public static void shareData(final Context ctx, final String title, final String content, final String image, final String url) {
        final Dialog mCameraDialog = new Dialog(ctx, R.style.MyDialog);
        final PlatformActionListener platformActionListener = new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                mCameraDialog.dismiss();
                //大部分的回调方法都处于网络线程，因此可以简单默认为回调方法都不在主线程.
                Toast.makeText(ctx, "分享成功！", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("TAG", throwable.getMessage());
                Toast.makeText(ctx, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        };
        View root = LayoutInflater.from(ctx).inflate(
                R.layout.lib_share, null);
//        root.findViewById(R.id.btn_open_camera).setOnClickListener(btnlistener);
//        root.findViewById(R.id.btn_choose_img).setOnClickListener(btnlistener);
        root.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.dismiss();
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(params);
        root.findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("share",title+"..."+content+"..."+url+"..."+image);
                Wechat.ShareParams wechatshare = new Wechat.ShareParams();
                wechatshare.setShareType(Platform.SHARE_WEBPAGE);//非常重要：一定要设置分享属性
                wechatshare.setTitle(title); //分享标题
                wechatshare.setText(content);   //分享文本
                wechatshare.setUrl(url);
                wechatshare.setImageUrl(image);
                //wechatshare.setImagePath("android.resource://" + mContext.getPackageName() + "/" + R.mipmap.splash_logo);
                //wechatshare.setImageUrl(pic);
                //wechatshare.setImageUrl(UserBean.getInstance().getHead_pic());
                // wechatshare.setUrl("http://www.baidu.com");   //网友点进链接后，可以看到分享的详情
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(platformActionListener); // 设置分享事件回调
                // 执行分享
                wechat.share(wechatshare);
            }
        });
        root.findViewById(R.id.qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QQ.ShareParams qqShare = new QQ.ShareParams();
                qqShare.setShareType(Platform.SHARE_WEBPAGE); //非常重要：一定要设置分享属性
                qqShare.setTitle(title);
                qqShare.setText(content);
                qqShare.setTitleUrl(url);  //网友点进链接后，可以看到分享的详情
                qqShare.setImageUrl(image);
                //3、非常重要：获取平台对象
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(platformActionListener); // 设置分享事件回调
                // 执行分享
                qq.share(qqShare);
            }
        });
        mCameraDialog.show();
    }

}
