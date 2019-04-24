package com.konka.renting.landlord.user.userinfo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konka.renting.R;
import com.konka.renting.utils.RxBus;

/**
 * Created by Administrator on 2017/7/26.
 */

public abstract class PhotoBitmapSelect extends PopupWindow implements View.OnClickListener{
//    private String path = Environment.getExternalStorageDirectory().toString() + "/lefulyy/icon_bitmap/" + "touxiang.jpg";  //头像的本地保存路径

    /**
     * 打开相册
     */
    public static final int OPEN_IMAGE=101;

    /**
     * 打开相机
     */
    public static final int OPEN_CAMERA=102;

    /**
     * 裁剪图片
     */
    public static final int CUT_PHOTO=103;

    private TextView btnPhoto,btnSelect,btnCancel;    //popupwindow控件
    private View mview;   //视图
    private String photoPath;    // 图片保存路径
    private Context mcontext;
    private Uri cropuri;

    public PhotoBitmapSelect(Context context){
        super(context);
        this.mcontext=context;
        init(context);   //popup布局
        setPopupWindow();   //布局属性
    }
    /**
     * 初始化布局
     */
    private void init(Context context) {
        LayoutInflater infalter= LayoutInflater.from(context);     //popup显示界面
        mview=infalter.inflate(R.layout.photo_popup_select,null);    //绑定布局
        //获取控件
        btnPhoto= (TextView) mview.findViewById(R.id.id_btn_take_photo);
        btnSelect= (TextView) mview.findViewById(R.id.id_btn_select);
        btnCancel= (TextView) mview.findViewById(R.id.id_btn_cancel);

        btnPhoto.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void setPopupWindow() {
        this.setContentView(mview); //设置View
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);  //弹出窗宽度
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT); //弹出高度
        this.setFocusable(true);  //弹出窗可触摸 WindowManager windowManager=getWin
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));   //设置背景透明
        this.setAnimationStyle(R.style.mypopupwindow_anim_style);   //弹出动画
        mview.setOnTouchListener(new View.OnTouchListener() {   //如果触摸位置在窗口外面则销毁
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height=mview.findViewById(R.id.id_pop_layout).getTop();
                int y= (int) event.getY();
                if (event.getAction()== MotionEvent.ACTION_UP){
                    if (y<height){
                        dismiss();   //销毁
                    }
                }
                return true;
            }
        });
    }

    /**
     * 显示popupWindow图片选择窗口
     */
    public void showPopupSelect(View view) {
        showAtLocation(view, Gravity.CENTER| Gravity.CENTER_VERTICAL,0,0);
    }

    /**
     * 关闭popupwindow
     */
    private void closePopupSelect() {
        dismiss();
    }

    /**
     * bitmap 选择方式——拍照，相册
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_take_photo:    //拍照
                RxBus.getDefault().post(new PhotoEvent(1));
                closePopupSelect(); //关闭popupwindow
                break;
            case R.id.id_btn_select:        //相册选择
                RxBus.getDefault().post(new PhotoEvent(2));
                closePopupSelect();
                break;
            case R.id.id_btn_cancel:        //取消
                closePopupSelect();  //关闭popupwindow
                break;
            default:
        }
    }

    /*public abstract void onStartActivityForResult(Intent intent, int requestCode);

    private void selectPhotoAlbum() {   //打开相册
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        onStartActivityForResult(intent,OPEN_IMAGE);
    }

    private void addPhotoBitmap() {    //开始拍照
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //设置图片要保存的 根路径+文件名
        photoPath= PhotoBitmapUtil.getPhotoFileName(mcontext);
        File file=new File(photoPath);
        if (!file.exists()) {
           *//* try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }*//*
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));    // uri 路径
            onStartActivityForResult(intent, OPEN_CAMERA);
        }
    }

    *//**
     * 获取图片的地址
     * @param requestCode
     * @param resultCode
     * @param data
     *//*
    public String getPhotoResultPath(int requestCode, int resultCode, Intent data){
        if (resultCode!= Activity.RESULT_OK){
            return null;
        }
        //相机返回
        if (requestCode==OPEN_CAMERA && photoPath!=null){
            CutPhoto(Uri.fromFile(new File(photoPath)));
        }else if (requestCode==OPEN_IMAGE){   //相册返回
            // 内容提供者标示
            Uri newUri = Uri.parse(PhotoUtils.getPath(mcontext,data.getData()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                newUri = FileProvider.getUriForFile(mcontext,"com.konka.fileProvider", new File(newUri.getPath()));
            }
            CutPhoto(newUri);
        } else if (requestCode==CUT_PHOTO) {  //裁剪图片
            if (data!=null){
                Bundle bundle=data.getExtras();
                Bitmap bitmap= (Bitmap) bundle.get("data");
                String path=PhotoBitmapUtil.savePhotoToSD(bitmap,mcontext);
                if (path!=null){
                    return path;
                }else {
                    return null;
                }
            }
        }
        return null;
    }

    private void CutPhoto(Uri uri) {        // 裁剪图片
        Intent intent=new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image*//*");
        //intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);  //aspectX aspectY 是宽高的比例，根据自己情况修改
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        // 图片像素不够不能占满全屏加上下面的这两句之后，系统就会把图片给拉伸
        intent.putExtra("scale",true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoPath);
        onStartActivityForResult(intent,CUT_PHOTO);
    }*/
}
