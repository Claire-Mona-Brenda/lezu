package com.konka.renting.tenant.payrent.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

import com.konka.renting.R;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.Img;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.http.RetrofitHelper;
import com.konka.renting.http.subscriber.CommonSubscriber;
import com.konka.renting.landlord.house.data.MissionEnity;
import com.konka.renting.landlord.house.widget.IPopBack;
import com.konka.renting.landlord.house.widget.PicRecordWidget;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.utils.RxUtil;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ATI on 2018/3/31.
 */

public class HouseACHActivity extends Activity implements View.OnClickListener {
    TextView xh;
    TextView status;
    TextView adress;
    TextView price;
    TextView people_num;
    TextView dis_time;
    AppCompatImageView imageView;
    PayOrder missionEnity;
    TextView total;
    PicRecordWidget picRecordWidget;
    StringBuffer imgname = new StringBuffer();
    RatingBar ratingBar;
        EditText content;
    private String filepath;
    private String filename;
    CheckBox nm;
    RxPermissions rxPermissions;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_house_ach_activity);
        missionEnity = (PayOrder) getIntent().getParcelableExtra("pay");
        total = findViewById(R.id.total);
        nm=findViewById(R.id.nm);
        xh = (TextView) findViewById(R.id.xh);
        status = (TextView) findViewById(R.id.status);
        adress = (TextView) findViewById(R.id.adress);
        price = (TextView) findViewById(R.id.price);
        status = (TextView) findViewById(R.id.status);
        dis_time = (TextView) findViewById(R.id.dis_time);
        imageView = (AppCompatImageView) findViewById(R.id.img_house);
        rxPermissions=new RxPermissions(this);

        ratingBar=findViewById(R.id.ratingBar);
        ratingBar.setStepSize(1);
        content=findViewById(R.id.content);
        picRecordWidget = findViewById(R.id.pic);
        picRecordWidget.setIPOP(new IPopBack() {
            @Override
            public void callBack(String s) {
                popBack(s);
            }

            @Override
            public void delCallBank(int index) {

            }
        });
        bindData();
        getData();
    }

    public void submitMultiImg() {
        List<MissionEnity> list = picRecordWidget.getImgs();
        for (MissionEnity missionEnity : list) {
            submitImg(missionEnity);
        }

    }

    public void submitImg(MissionEnity missionEnity) {
        rx.Observable<DataInfo<Img>> observable = null;

        File file = new File(missionEnity.imgpath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        imgname.append(file.getName()).append(",");
// MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody fullName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), missionEnity.imgname);
        observable = RetrofitHelper.getInstance().uploadImage(fullName, body);
        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<Img>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<Img>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo <Img>homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            Toast.makeText(HouseACHActivity.this, "图像成功", Toast.LENGTH_SHORT).show();
//                            homeInfoDataInfo.data();
//                            bindData(homeInfoDataInfo.data());
                        } else {
                            Toast.makeText(HouseACHActivity.this, "图像失败", Toast.LENGTH_SHORT).show();
//                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    private CompositeSubscription mCompositeSubscription;

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    public void bindData() {
        Picasso.get().load(missionEnity.roomInfo.image).into(imageView);
        xh.setText("订单编号：" + missionEnity.merge_order_no);
        adress.setText(missionEnity.roomInfo.address);
        price.setText("￥" + missionEnity.deposit_price);
        status.setText(missionEnity.status_name);
        dis_time.setText(missionEnity.start_time + "-" + missionEnity.end_time);

    }

    public void bindData2Order(RoomInfo.RoomDescription room) {
        if ((!TextUtils.isEmpty(room.housing_price)) && (!TextUtils.isEmpty(room.housing_price)) && (!TextUtils.isEmpty(room.housing_price))) {
            double a = Double.parseDouble(room.housing_price);
            double b = Double.parseDouble(room.housing_price);
            double c = Double.parseDouble(room.housing_price);
            double res = a + b + c;
            total.setText(res + "￥");
        }


    }

    public void getData() {
        rx.Observable<DataInfo<RoomInfo>> observable = null;
        observable = RetrofitHelper.getInstance().getRoomInfo(missionEnity.room_id);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo<RoomInfo>>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo<RoomInfo>>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia", "");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo<RoomInfo> homeInfoDataInfo) {
//                        dismiss();

                        if (homeInfoDataInfo.success()) {
                            homeInfoDataInfo.data();
                            RoomInfo roomInfo = homeInfoDataInfo.data();
                            bindData2Order(roomInfo.info);
                        } else {
//                            showToast(homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back){
            this.finish();
        }else{
            submitMultiImg();
            submitData();
        }

    }
    public void submitData(){
        String isnm;
        if(nm.isChecked()){
            isnm="1";
        }else{
            isnm="0";
        }
        String txt=content.getText().toString();
        String ra=ratingBar.getRating()+"";
        String res = imgname.toString();
        if (!TextUtils.isEmpty(res)) {
            res = res.substring(0, res.length() - 1);
        }
        rx.Observable<DataInfo> observable = null;
        observable= RetrofitHelper.getInstance().addRoomComment(missionEnity.merge_order_no,missionEnity.room_id,txt,ra,isnm,res);

        Subscription subscription = (observable
                .compose(RxUtil.<DataInfo>rxSchedulerHelper())
                .subscribe(new CommonSubscriber<DataInfo>() {
                    @Override
                    public void onError(Throwable e) {
//                        dismiss();
//                        doFailed();
                        Log.d("jia","");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(DataInfo homeInfoDataInfo) {

                        if (homeInfoDataInfo.success()) {
                            HouseACHActivity.this.finish();
                            ShowToastUtil.showSuccessToast(HouseACHActivity.this,homeInfoDataInfo.msg());
                        } else {
                            ShowToastUtil.showWarningToast(HouseACHActivity.this,homeInfoDataInfo.msg());
                        }
                    }
                }));
        addSubscrebe(subscription);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + ".jpg";
                File f = saveBitmap(imageFileName, imageBitmap);
                filepath = f.getAbsolutePath();
                filename = imageFileName;
//                LruBitmap.addBitmap(filepath,imageBitmap);
                MissionEnity missionEnity = new MissionEnity();
                missionEnity.imgpath = filepath;
                missionEnity.imgname = filename;
                picRecordWidget.addImg(missionEnity);

            } else {
                if (data != null) {
                    Uri selectedImageUri = data.getData();

                    if (selectedImageUri != null) {

                        String s2 = getRealPathFromURI(selectedImageUri);
                        filepath = s2;
                        // Bitmap b = BitmapFactory.decodeFile(filepath);
                        String s[] = filepath.split("/");
                        filename = s[s.length - 1];
                        //  LruBitmap.addBitmap(filepath,b);
                        MissionEnity missionEnity = new MissionEnity();
                        missionEnity.imgpath = filepath;
                        missionEnity.imgname = filename;
                        picRecordWidget.addImg(missionEnity);
                    }

                }
            }

        }
    }

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;

    public void popBack(String str) {
        final  String s=str;
        rxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (s.equals("相机")) {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePictureIntent, 1);
                            } else if (s.equals("图库")) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 2);
                            } else {


                            }
                        }
                    }
                });


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getRealPathFromURI(Uri contentURI) {
        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) {
//            // Source is Dropbox or other similar local file path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
        result = getPath(this, contentURI);
        return result;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public File saveBitmap(String picName, Bitmap bm) {
        File f = new File("/sdcard/konka/", picName);

        File dir = new File("/sdcard/konka/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
