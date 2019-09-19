package com.konka.renting.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.konka.renting.base.BaseApplication;
import com.konka.renting.bean.AddBankInfo;
import com.konka.renting.bean.AddRentingBean;
import com.konka.renting.bean.AgentBean;
import com.konka.renting.bean.AlipayBean;
import com.konka.renting.bean.BillBean;
import com.konka.renting.bean.CheckGatewayStatusBean;
import com.konka.renting.bean.CheckVersionBean;
import com.konka.renting.bean.CityInfo;
import com.konka.renting.bean.CollectionListBean;
import com.konka.renting.bean.CommentTemp;
import com.konka.renting.bean.ConfigInfoBean;
import com.konka.renting.bean.ContractBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.DevicesOpenPasswordBean;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.bean.GetFaceVerfyBean;
import com.konka.renting.bean.GetIssueBankBean;
import com.konka.renting.bean.HomeInfo;
import com.konka.renting.bean.IdCardFrontbean;
import com.konka.renting.bean.Img;
import com.konka.renting.bean.KInfo;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.bean.MessageBean;
import com.konka.renting.bean.MessageDetailBean;
import com.konka.renting.bean.MyBankBean;
import com.konka.renting.bean.OpenDoorListInfo;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.OrderBean;
import com.konka.renting.bean.OrderDetailBean;
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.bean.OrderRentingHistoryBean;
import com.konka.renting.bean.PayOrder;
import com.konka.renting.bean.ProfileBean;
import com.konka.renting.bean.PwsOrderDetailsBean;
import com.konka.renting.bean.RechargeBean;
import com.konka.renting.bean.RoomDes2;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.bean.ServerDeviceInfo;
import com.konka.renting.bean.ServicePackageBean;
import com.konka.renting.bean.ShareBean;
import com.konka.renting.bean.TenantDespoitDetailBean;
import com.konka.renting.bean.TenantDespoitlistBean;
import com.konka.renting.bean.TenantManagerBean;
import com.konka.renting.bean.TenantOrderDetailBean;
import com.konka.renting.bean.TenantUserinfoBean;
import com.konka.renting.bean.UploadRecordBean;
import com.konka.renting.bean.UserInfoBean;
import com.konka.renting.bean.WebUrlInfo;
import com.konka.renting.bean.WithDrawRecordBean;
import com.konka.renting.bean.WxPayInfo;
import com.konka.renting.http.api.KonkaApiService;
import com.konka.renting.landlord.house.entity.DicEntity;
import com.konka.renting.landlord.house.entity.RoomList;
import com.konka.renting.landlord.user.rentoutincome.RentOutIncomeBean;
import com.konka.renting.location.LocationInfo;
import com.konka.renting.location.LocationUtils;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.utils.NetWorkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;


/**
 * @author jzxiang
 *         create at 7/6/17 23:16
 */
public class RetrofitHelper {
    private static final String TAG = "RetrofitHelper";
    public static final String PATH_DATA = BaseApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
    private static RetrofitHelper mRetrofitHelper = null;
    private static Retrofit mRetrofit;
    private static KonkaApiService mApiService;

    private RetrofitHelper() {

    }

    public static RetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (mRetrofitHelper == null) mRetrofitHelper = new RetrofitHelper();
                mRetrofit = createRetrofit(new Retrofit.Builder(), createOkHttpClient(new OkHttpClient.Builder()), KonkaApiService.HOST);
                mApiService = mRetrofit.create(KonkaApiService.class);
            }
        }

        return mRetrofitHelper;
    }

    private static OkHttpClient createOkHttpClient(OkHttpClient.Builder builder) {
        // HttpLoggingInterceptor 打印请求到的json字符串和查看log
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        File cacheFile = new File(RetrofitHelper.PATH_CACHE); //缓存文件夹
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); // 创建缓存对象 缓存大小为50M
        // 自定义缓存拦截器
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetWorkUtil.isNetworkConnected()) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE) // FORCE_CACHE只取本地的缓存 FORCE_NETWORK常量用来强制使用网络请求
                            .header("x-access-token", getToken()) // <-- this is the important line
                            .build();
                } else {
                    if (!TextUtils.isEmpty(getToken())) {
                        Request.Builder requestBuilder = request.newBuilder()
                                .header("x-access-token", getToken()); // <-- this is the important line
                        request = requestBuilder.build();
                    }
                }

                Response response = chain.proceed(request);
                if (NetWorkUtil.isNetworkConnected()) {
                    int maxAge = 0;
                    // 有网络时, 不缓存, 最大保存时长为0
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
        builder.addInterceptor(cacheInterceptor);
        //设置缓存
        //builder.addNetworkInterceptor(cacheInterceptor);
        //builder.addInterceptor(cacheInterceptor);
        //builder.cache(cache);
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
//        builder.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//                Request.Builder requestBuilder = original.newBuilder();
//                if (original.body() instanceof FormBody) {
//                    FormBody.Builder newFormBody = new FormBody.Builder();
//                    FormBody oldFormBody = (FormBody) original.body();
//                    for (int i = 0; i < oldFormBody.size(); i++) {
//                        newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
//                    }
//                    newFormBody.add("os", "android");
//                    requestBuilder.method(original.method(), newFormBody.build());
//                } else if (original.body() instanceof MultipartBody) {
//                    MultipartBody.Builder newFormBody = new MultipartBody.Builder();
//                    // 默认是multipart/mixed，大坑【主要是我们php后台接收时头信息要求严格】
//                    newFormBody.setType(MediaType.parse("multipart/form-data"));
//                    MultipartBody oldFormBody = (MultipartBody) original.body();
//                    for (int i = 0; i < oldFormBody.size(); i++) {
//                        newFormBody.addPart(oldFormBody.part(i));
//                    }
//                    newFormBody.addFormDataPart("os", "android");
//                    requestBuilder.method(original.method(), newFormBody.build());
//                } else if (TextUtils.equals(original.method(), "POST")) {
//                    FormBody.Builder newFormBody = new FormBody.Builder();
//                    newFormBody.add("os", "android");
//                    requestBuilder.method(original.method(), newFormBody.build());
//                }
//
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        })
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(final Chain chain) throws IOException {
                // 原始请求
                Request request = chain.request();
                Response response = chain.proceed(request);
                ResponseBody responseBody = response.body();
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                String respString = source.buffer().clone().readString(Charset.defaultCharset());
                Log.d(TAG, "--->返回报文，respString = " + respString);
                // TODO 这里判断是否是登录超时的情况
                JSONObject j = null;
                try {
                    j = new JSONObject(respString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 这里与后台约定的状态码700表示登录超时【后台是java，客户端自己维护cookie，没有token机制。但此处如果刷新token，方法也一样】
//                if (j != null && j.optInt("status") == -1) {
//                    Log.e(TAG, "--->登录失效，自动重新登录");
//                    if (BaseActivity.getForegroundActivity() != null) {
//                        LoginUserBean.getInstance().reset();
//                        if (LoginUserBean.getInstance().isLandlord()) {
//                            LoginNewActivity.toLandlordActivity(BaseActivity.getForegroundActivity());
//                        } else {
//                            LoginNewActivity.toTenantActivity(BaseActivity.getForegroundActivity());
//                        }
//                        BaseActivity.getForegroundActivity().finish();
//                    }
//
//                    throw new RuntimeException("请重新登录");
//                    // 判断是否登录成功了
//
//                }
                return response;
            }
        });


        //错误重连
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }

    private static Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String url) {
        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();//使用 gson coverter，统一日期请求格式
        return builder
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)) //GsonConverterFactory.create()
                .build();
    }

    public Observable<DataInfo<KInfo<LoginInfo>>> register_landlord(String mobile, String password, String verify) {
        return mApiService.register_landlord(mobile, password, verify);
    }

    public Observable<DataInfo<KInfo<LoginInfo>>> register_renter(String mobile, String password, String verify) {
        return mApiService.register_renter(mobile, password, verify);
    }

    public Observable<DataInfo> sendVerifyCode(int type, String mobile, int isFindPwd) {
        return mApiService.sendVerCode(type, mobile, isFindPwd);
    }

    public Observable<DataInfo> verifyCodeRenter(String mobile, String code) {
        return mApiService.verifyCodeRenter(mobile, code);
    }

    public Observable<DataInfo> verifyCodeLandLord(String mobile, String code) {
        return mApiService.verifyCodeLandLord(mobile, code);
    }

    public Observable<DataInfo<LoginInfo>> login_landlord(String mobile, String password) {
        return mApiService.login_landlord(mobile, password);
    }

    public Observable<DataInfo<LoginInfo>> login_renter(String mobile, String password) {
        return mApiService.login_renter(mobile, password);
    }

    public Observable<DataInfo<UserInfoBean>> getUserInfo(String token) {
        return mApiService.getUserInfo(token);
    }

    public Observable<DataInfo> landmoreLoginOut(String token) {
        return mApiService.landmoreLoginOut(token);
    }

    public Observable<DataInfo<TenantUserinfoBean>> getTenantUserInfo(String token) {
        return mApiService.getTenantUserInfo(token);
    }

    public Observable<DataInfo> tenantLoginout(String token) {
        return mApiService.tenantLoginout(token);
    }

    public Observable<DataInfo<HomeInfo>> getHomeLandlordData(String city_id) {
        return mApiService.getHomeLandlordData(getToken(), city_id, 1);
    }

    public Observable<DataInfo<HomeInfo>> getHomeRenterData(String city_id) {
        return mApiService.getHomeRenterData(getToken(), city_id, 1);
    }

    /**
     * 获取闲置房间
     *
     * @return
     */
    public Observable<DataInfo<ListInfo<RoomInfo>>> getLandlordFreeRoomListData(int page) {
        return mApiService.getLandlordRoomListData(getToken(), "0", page);
    }

    public Observable<DataInfo<CityInfo>> getHotCity() {
        return mApiService.getHotCity();
    }

    /**
     * 已租出
     *
     * @return
     */
    public Observable<DataInfo<ListInfo<RoomInfo>>> getLandloarRoomListRented(int page) {
        return mApiService.getLandlordRoomList(getToken(), 1, page);
    }

    /**
     * 未租出
     *
     * @return
     */
    public Observable<DataInfo<ListInfo<RoomInfo>>> getLandloarRoomListUnRented(int page) {
        return mApiService.getLandlordRoomList(getToken(), 0, page);
    }


    /**
     * 即将到期
     *
     * @return
     */
    public Observable<DataInfo<ListInfo<RoomInfo>>> getSoonExpireRoomList(int page) {
        return mApiService.getSoonExpireRoomList(getToken(), page);
    }

    public Observable<DataInfo<ListInfo<OrderInfo>>> getReadingList(int page) {
        return mApiService.getReadingList(getToken(), page);
    }

    /**
     * 获取初始水电
     *
     * @param room_id
     * @return
     */
    public Observable<DataInfo<OrderInfo>> getReadingInfo(String room_id) {
        return mApiService.getReadingInfo(getToken(), room_id);
    }

    public Observable<DataInfo> addHydropowerOrder(String order_no, String end_electric, String end_water) {
        return mApiService.addHydropowerOrder(getToken(), order_no, end_electric, end_water);
    }

    public Observable<DataInfo<ListInfo<OrderInfo>>> getOrdered(int page) {
        return mApiService.getOrder(getToken(), 1, page);
    }

    public Observable<DataInfo<ListInfo<OrderInfo>>> getUnOrdered(int page) {
        return mApiService.getOrder(getToken(), 0, page);
    }


    private static String getToken() {
        return LoginUserBean.getInstance().getAccess_token();
    }

    public Observable<DataInfo<DicEntity>> getLeaseType() {
        return mApiService.getLeaseType(LoginUserBean.getInstance().getAccess_token());
    }

    public Observable<DataInfo<DicEntity>> getAllLetterCity() {
        return mApiService.getAllLetterCity();
    }

    public Observable<DataInfo<DicEntity>> getRoomType() {
        return mApiService.getRoomType(LoginUserBean.getInstance().getAccess_token());
    }

    public Observable<DataInfo<DicEntity>> getOrientation() {
        return mApiService.getOrientation(LoginUserBean.getInstance().getAccess_token());
    }

    public Observable<DataInfo<DicEntity>> getConfig() {
        return mApiService.getConfig(LoginUserBean.getInstance().getAccess_token());
    }

    public Observable<DataInfo<RoomList>> getLandlordRoomList() {
        return mApiService.getLandlordRoomList(LoginUserBean.getInstance().getAccess_token());
    }

    public Observable<DataInfo<RoomList>> getRoomList(String city_id,
                                                      String lat,
                                                      String lng) {
        return mApiService.getRoomList(city_id, lat, lng);
    }

    public Observable<DataInfo<RoomInfo>> getRoom(String id) {
        return mApiService.getRoom(LoginUserBean.getInstance().getAccess_token(), id);
    }

    public Observable<DataInfo> LandlordRenterupdateRoom(String roomid, String is_on_sale) {
        return mApiService.LandlordRenterupdateRoom(LoginUserBean.getInstance().getAccess_token(), roomid, is_on_sale);
    }

    public Observable<DataInfo> LandlordMyreturnBond(String roomid) {
        return mApiService.LandlordMyreturnBond(LoginUserBean.getInstance().getAccess_token(), roomid);
    }

    public Observable<DataInfo> addApplyRenting(String roomid) {
        return mApiService.addApplyRenting(LoginUserBean.getInstance().getAccess_token(), roomid);
    }

    public Observable<DataInfo> cancelCheckOut(String id) {
        return mApiService.cancelCheckOut(LoginUserBean.getInstance().getAccess_token(), id);
    }

    public Observable<DataInfo<HashMap>> getBond() {
        LocationInfo locationInfo = LocationUtils.getInstance();
        return mApiService.getBond("1", "1", "1");
//        return mApiService.boonOrderWechatPay("renter_ce6cb60621add9d34c1409b32909fd6b", "12428087335022");
    }

    public Observable<DataInfo<WxPayInfo>> boonOrderWechatPay(String id) {
        return mApiService.boonOrderWechatPay(LoginUserBean.getInstance().getAccess_token(), id);
//        return mApiService.boonOrderWechatPay("renter_ce6cb60621add9d34c1409b32909fd6b", "12428087335022");
    }

    public Observable<DataInfo> boonOrderBalancePay(String id) {
        return mApiService.boonOrderBalancePay(LoginUserBean.getInstance().getAccess_token(), id);
    }

    public Observable<DataInfo<AlipayBean>> boonOrderAlipayPay(String id) {
        return mApiService.boonOrderAlipayPay(LoginUserBean.getInstance().getAccess_token(), id);
//        return mApiService.boonOrderWechatPay("renter_ce6cb60621add9d34c1409b32909fd6b", "12428087335022");
    }

    public Observable<DataInfo<AlipayBean>> orderAlipayPay(String id) {
        return mApiService.orderAlipayPay(LoginUserBean.getInstance().getAccess_token(), id);
    }

    public Observable<DataInfo<WxPayInfo>> orderWechatPay(String id) {
//        return mApiService.orderWechatPay(LoginUserBean.getInstance().getAccess_token(), id);
        return mApiService.orderWechatPay(getToken(), id);
    }

    public Observable<DataInfo<Img>> uploadImage(RequestBody fullName, MultipartBody.Part file) {
        return mApiService.uploadImage(fullName, file);
    }

    public Observable<DataInfo> requestRoomgetVerify(String pnum) {
        return mApiService.requestRoomgetVerify("0", pnum, "4");
    }

    public Observable<DataInfo> addRoomComment(
            String order_no,
            String room_id,
            String content,
            String score,
            String is_anonymous,
            String image_list) {
        return mApiService.addRoomComment(LoginUserBean.getInstance().getAccess_token(), order_no,
                room_id,
                content,
                score,
                is_anonymous,
                image_list);
    }

    public Observable<DataInfo<RoomInfo>> addRoom(
            String gateinfo, String gateway,
            String room_name, String lease_type_id,
            String room_type_id,
            String orientation_id,
            String housing_price,
            String deposit_price,
            String measure_area,
            String explain,
            String province_id,
            String city_id,
            String area_id,
            String address,
            String lat,
            String lng,
            String building_no,
            String door_no,
            String total_floor,
            String floor,
            String door_number,
            String image,
            String initial_water,
            String initial_electric,
            String water_rent,
            String electric_rent,
            String litter_rent,
            String property_rent,
            String cost_rent,
            String config_id,
            String config,
            String agent_id) {
        return mApiService.addRoom(LoginUserBean.getInstance().getAccess_token(),
                gateinfo, gateway,
                room_name, lease_type_id,
                room_type_id,
                orientation_id,
                housing_price,
                deposit_price,
                measure_area,
                explain,
                province_id,
                city_id,
                area_id,
                address,
                lat,
                lng,
                building_no,
                door_no,
                total_floor,
                floor,
                door_number,
                image,
                initial_water,
                initial_electric,
                water_rent,
                electric_rent,
                litter_rent,
                property_rent,
                cost_rent,
                config_id,
                config,
                agent_id
        );

    }

    public Observable<DataInfo<String>> perfectRoom(
            String type,
            String id,
            String housing_price,
            String initial_water,
            String initial_electric,
            String water_rent,
            String electric_rent,
            String litter_rent,
            String property_rent,
            String cost_rent
    ) {
        return mApiService.perfectRoom(LoginUserBean.getInstance().getAccess_token(),
                type,
                id,
                housing_price,
                initial_water,
                initial_electric,
                water_rent,
                electric_rent,
                litter_rent,
                property_rent,
                cost_rent
        );

    }

    public Observable<DataInfo<String>> perfectRoom(
            String type,
            String id,
            String housing_price,
            String initial_water,
            String initial_electric,
            String water_rent,
            String electric_rent,
            String litter_rent,
            String property_rent,
            String cost_rent,
            String server_pay_type
    ) {
        return mApiService.perfectRoom(LoginUserBean.getInstance().getAccess_token(),
                type,
                id,
                housing_price,
                initial_water,
                initial_electric,
                water_rent,
                electric_rent,
                litter_rent,
                property_rent,
                cost_rent,
                server_pay_type
        );

    }

    public Observable<DataInfo<String>> updateRoom(
            String gateinfo, String gateway,
            String room_name,
            String id,
            String lease_type_id,
            String room_type_id,
            String orientation_id,
            String housing_price,
            String deposit_price,
            String measure_area,
            String explain,
            String province_id,
            String city_id,
            String area_id,
            String address,
            String lat,
            String lng,
            String building_no,
            String door_no,
            String total_floor,
            String floor,
            String door_number,
            String image,
            String initial_water,
            String initial_electric,
            String water_rent,
            String electric_rent,
            String litter_rent,
            String property_rent,
            String cost_rent,
            String config_id,
            String config,
            String imgs
    ) {
        return mApiService.updateRoom(LoginUserBean.getInstance().getAccess_token(),
                gateinfo, gateway,
                room_name, id,
                lease_type_id,
                room_type_id,
                orientation_id,
                housing_price,
                deposit_price,
                measure_area,
                explain,
                province_id,
                city_id,
                area_id,
                address,
                lat,
                lng,
                building_no,
                door_no,
                total_floor,
                floor,
                door_number,
                image,
                initial_water,
                initial_electric,
                water_rent,
                electric_rent,
                litter_rent,
                property_rent,
                cost_rent,
                config_id,
                config,
                imgs
        );

    }

    //    房间ID（room_id）：
//    入住时间（start_time）：
//    退房时间（end_time）：
//    租金类型（rent_type_id）：
//    交租方式（renting_mode）：
//    房租押金（deposit_price）：
//    房租/月（housing_price）：
//    姓名（name）：
//    手机号码（tel）：
//    验证码（code）：
//    身份证号（identity）：
    public Observable<DataInfo> addRenting(
            String room_id,
            String start_time,
            String end_time,
            String rent_type_id,
            String renting_mode,
            String housing_price,
            String name,
            String tel,
            String code,
            String identity,
            String buyer_remark
    ) {
        return mApiService.addRenting(LoginUserBean.getInstance().getAccess_token(), room_id, start_time, end_time, rent_type_id, renting_mode, housing_price, name, tel, code, identity, buyer_remark);

    }

    public Observable<DataInfo<RoomInfo>> getRoomInfo(String id) {
        return mApiService.getRoomInfo(LoginUserBean.getInstance().getAccess_token(), id);
    }

    public Observable<DataInfo<CommentTemp>> getRoomCommentList(String rooid, int p) {
        return mApiService.getRoomCommentList(rooid, p);
    }

    public Observable<DataInfo<RoomDes2>> getHistoryRoomList(String id, int p) {
        return mApiService.getHistoryRoomList(LoginUserBean.getInstance().getAccess_token(), id, p);
    }

    public Observable<DataInfo<PayOrder>> getRenterRoomList(String status, int p) {
        return mApiService.getRenterRoomList(LoginUserBean.getInstance().getAccess_token(), status, p);
    }

    public Observable<DataInfo<DicEntity>> getRenterOrderLeaseType() {
        return mApiService.getRenterOrderLeaseType();
    }
    public Observable<DataInfo<AgentBean>> getAgent() {
        return mApiService.getAgent();
    }
    public Observable<DataInfo> deleteRoom(String id) {
        return mApiService.deleteRoom(LoginUserBean.getInstance().getAccess_token(), id);
    }

    public Observable<DataInfo> updateRenting(String merge_order_no,
                                              String refund_reason,
                                              String state) {
        return mApiService.updateRenting(LoginUserBean.getInstance().getAccess_token(), merge_order_no,
                refund_reason, state);
    }

    public Observable<DataInfo> updateRentingMode(String renting_mode,
                                                  String order_no) {
        return mApiService.updateRentingMode(LoginUserBean.getInstance().getAccess_token(), renting_mode,
                order_no);
    }

    public Observable<DataInfo<PayOrder>> getHistoryOrderList(String no,int p) {
        return mApiService.getHistoryOrderList(LoginUserBean.getInstance().getAccess_token(),no,p);
    }

    public Observable<DataInfo<ListInfo<RoomInfo>>> getSearchRoomList(String keyword, String city_id) {
        return mApiService.searchRoomList(keyword, city_id);
    }

    public Observable<DataInfo<ListInfo<RoomInfo>>> getMoreRoomList(int p, String city_id) {
        return mApiService.getMoreRoomList(p, city_id);
    }

    public Observable<DataInfo> resetPasswordLandlord(String mobile, String newPwd, String verify) {
        return mApiService.resetPasswordLandlord(mobile, newPwd, newPwd, verify);
    }

    public Observable<DataInfo> resetPasswordRenter(String mobile, String newPwd, String verify) {
        return mApiService.resetPasswordRenter(mobile, newPwd, newPwd, verify);
    }

    public Observable<DataInfo<KInfo<LocationInfo>>> location(String city_name) {
        return mApiService.location(city_name);
    }

    public Observable<DataInfo<LocationInfo.LatLng>> getLatLng(String city_id) {
        return mApiService.getLatLng(city_id);
    }

    public Observable<DataInfo<GetFaceVerfyBean>> getFaceVerfy() {
        return mApiService.getFaceVerfy(getToken());
    }

    public Observable<DataInfo<UploadRecordBean>> uploadRecord(MultipartBody.Part part) {
        return mApiService.uploadRecord(part);
    }

    public Observable<DataInfo> facrDetect(String verfy, String videoName) {
        return mApiService.faceDetect(getToken(), verfy, videoName);
    }

    public Observable<DataInfo<UploadRecordBean>> uploadPhoto(MultipartBody.Part part) {
        return mApiService.uploadPhoto(part);
    }

    public Observable<DataInfo> updateLandlordUserInfo(String headimgurl, String real_name, String sex, String birthday, String tel, String identity, String identity_just, String identity_back) {
        return mApiService.updateLandlordUserInfo(getToken(), headimgurl, real_name, sex, birthday, tel, identity, identity_just, identity_back);
    }

    public Observable<DataInfo> updaterenterUserInfo(String headimgurl, String real_name, String sex, String birthday, String tel, String identity, String identity_just, String identity_back) {
        return mApiService.updateRenterUserInfo(getToken(), headimgurl, real_name, sex, birthday, tel, identity, identity_just, identity_back);
    }

    public Observable<DataInfo<ListInfo<RentOutIncomeBean>>> getRentOutIncome(int page) {
        return mApiService.getRentOutIncome(getToken(), page);
    }

    public Observable<DataInfo<ListInfo<OrderBean>>> getLandlordOrderList(int status, int page) {
        return mApiService.getLandmoreOrderList(getToken(), status, page);
    }

    public Observable<DataInfo<UploadRecordBean>> uploadPhotos(List<MultipartBody.Part> parts) {
        return mApiService.uploadPhotos(parts);
    }

    public Observable<DataInfo> updateCheckout(String refund_image, int status, String orderNum) {
        return mApiService.updateCheckOut(getToken(), refund_image, status, orderNum);
    }

    public Observable<DataInfo> updateRenting(String orderNum, int state) {
        return mApiService.updateRenting(getToken(), orderNum, state);
    }

    public Observable<DataInfo> refuseOrder(String orderNum, int state) {
        return mApiService.updateCheckOut(getToken(), state, orderNum);
    }

    public Observable<DataInfo<OrderRentingHistoryBean>> getOrderRentingHistory(String orderNum, int page) {
        return mApiService.getOrderRentingHistory(getToken(), orderNum, page);
    }

    public Observable<DataInfo<OrderDetailBean>> getLandlordOrderDetail(String orderNum) {
        return mApiService.getLandlordOrderDetail(getToken(), orderNum);
    }

    public Observable<DataInfo> changepayType(String orderNum, int status) {
        return mApiService.changePayType(getToken(), orderNum, status);
    }

    public Observable<DataInfo> openDoor(String gatewayID, String device_id) {

        return mApiService.openDoor(getToken(), gatewayID, device_id);
    }

    public Observable<DataInfo<ListInfo<TenantDespoitlistBean>>> getTenantDespoit(int page) {
        return mApiService.getTenantDespoit(getToken(), page);
    }

    public Observable<DataInfo<TenantDespoitDetailBean>> getTenantDespoitDetail(String orderNum, int page) {
        return mApiService.getTenantDespoitDetail(getToken(), orderNum, page);
    }

    public Observable<DataInfo<ListInfo<BillBean>>> getBill(int status, int page) {
        return mApiService.getBillList(getToken(), status, page);
    }

    public Observable<DataInfo> reminderRent(String order_no) {
        return mApiService.reminderRent(getToken(), order_no);
    }

    public Observable<DataInfo<ListInfo<CollectionListBean>>> getReminder(int page) {
        return mApiService.getReminder(getToken(), page);
    }



    public Observable<DataInfo> applyWithDraw(String bankId, String amount) {
        return mApiService.applyWithdraw(getToken(), bankId, amount);
    }

    public Observable<DataInfo<ListInfo<WithDrawRecordBean>>> getWithDrawRecord(int page) {
        return mApiService.getWithDrawRecord(getToken(), page);
    }

    public Observable<DataInfo<ProfileBean>> getMyprofile(String yearMonth) {
        return mApiService.getMyProfile(getToken(), yearMonth);
    }

    public Observable<DataInfo<ListInfo<MessageBean>>> getMessagelist(int page) {
        return mApiService.getMessageList(getToken(), page);
    }

    public Observable<DataInfo<ListInfo<MessageBean>>> getRenterMessagelist(int page) {
        return mApiService.getRenterMessageList(getToken(), page);
    }

//    public Observable<DataInfo<MessageDetailBean>> getMessageDetail(String id) {
////        return mApiService.getMessageDetail(id);
//    }

    public Observable<DataInfo<MessageDetailBean>> getRenterMessageDetail(String id) {
        return mApiService.getRenterMessageDetail(id);
    }

    public Observable<DataInfo> reNew(String merge_order_no) {
        return mApiService.reNew(getToken(), merge_order_no);
    }

    public Observable<TenantManagerBean> getTenantManager(String ordernum) {
        return mApiService.getTenantManager(getToken(), ordernum);
    }

    public Observable<DataInfo> updateRenter(String id, String state) {
        return mApiService.updateRenter(getToken(), id, state);
    }

    public Observable<DataInfo> deleteRenter(String id) {
        return mApiService.deleteRenter(getToken(), id);
    }

    public Observable<DataInfo> bindMobile(String mobile, String verfy) {
        return mApiService.bindMobile(getToken(), mobile, verfy);
    }

    public Observable<DataInfo> renterbindMobile(String mobile, String verfy) {
        return mApiService.renterbindMobile(getToken(), mobile, verfy);
    }

    public Observable<DataInfo<TenantOrderDetailBean>> getTenantOrderDetail(String merge_order_no) {
        return mApiService.getTenantOrderDetail(getToken(), merge_order_no);
    }

    public Observable<DataInfo<IdCardFrontbean>> idCardFrontDectect(String image) {
        return mApiService.idCardFrontDectect(getToken(), image);
    }

    public Observable<DataInfo> identyFacedect(String real_name, String identity, String identity_just, String identity_back) {

        return mApiService.identityAuthentication(getToken(), real_name, identity, identity_just, identity_back);
    }

    public Observable<DataInfo> identyTenantFacedect(String real_name, String identity, String identity_just, String identity_back) {

        return mApiService.identityTentAuthentication(getToken(), real_name, identity, identity_just, identity_back);
    }

    public Observable<DataInfo<ServicePackageBean>> getServicePackage() {
        return mApiService.getServicePackage();
    }

    public Observable<DataInfo<WxPayInfo>> serviceOrderWechatPay(String orderNum, String number) {
        return mApiService.serviceOrderWechatPay(getToken(), orderNum, number);
    }

    public Observable<DataInfo<AlipayBean>> serviceOrderAliPay(String orderNum, String number) {
        return mApiService.serviceOrderAliPay(getToken(), orderNum, number);
    }

    public Observable<DataInfo<ListInfo<RechargeBean>>> getRechargeRecord(int page) {
        return mApiService.getRecharge(getToken(), page);
    }

    public Observable<DataInfo<WxPayInfo>> rechargeOrderWechat(String ammout) {
        return mApiService.rechargeOrderWechatPay(getToken(), ammout);
    }

    public Observable<DataInfo<AlipayBean>> rechargeOrderAli(String ammout) {
        return mApiService.rechargeOrderAliPay(getToken(), ammout);
    }

//    public Observable<DataInfo<ListInfo<DeviceInfo>>> getDeviceList(String room_id) {
//        return mApiService.getDeviceList(getToken(), room_id);
//    }

    public Observable<DataInfo<ListInfo<MachineInfo>>> getDeviceType() {
        return mApiService.getDeviceType();
    }

    public Observable<DataInfo> editDevice(String id, String name) {
        return mApiService.editDevice(getToken(), id, name);
    }

    public Observable<DataInfo> addGateway(String room_id, String name, String code, String password, String address) {
        return mApiService.addGateway(getToken(), room_id, name, code, password, address);
    }

    public Observable<DataInfo<ListInfo<GatewayInfo>>> getGatewayList(String room_id) {
        return mApiService.getGatewayList(getToken(), room_id);
    }

    public Observable<DataInfo> goBandingMode(String gateway_id) {
        return mApiService.goBandingMode(getToken(), gateway_id);
    }

    public Observable<DataInfo<ServerDeviceInfo>> checkBandingStatus(String gateway_id) {
        return mApiService.checkBandingStatus(getToken(), gateway_id);
    }

    public Observable<DataInfo<WebUrlInfo>> getWebUrl() {
        return mApiService.getWebUrl();
    }

    public Observable<DataInfo> renewTenant(String merge_order_no, String apply_start_time, String apply_end_time) {
        return mApiService.reNew(getToken(), merge_order_no, apply_start_time, apply_end_time);
    }

    public Observable<DataInfo<ServerDeviceInfo>> addDevice(String device_id,
                                                            String gateway_id,
                                                            String room_id,
                                                            String type_id,
                                                            String name) {
        return mApiService.addDevice(getToken(), device_id, gateway_id, room_id, type_id, name);
    }

    public Observable<DataInfo<ShareBean>> getShare() {
        return mApiService.getShareApp();
    }

    public Observable<DataInfo<ShareBean>> sharePassword(String roomid) {
        return mApiService.sharePassword(getToken(), roomid);
    }

    public Observable<DataInfo<String>> getGatewayPwd(String roomid) {
        return mApiService.getGatewayPwd(getToken(), roomid);
    }

    public Observable<DataInfo> confirmRenting(String order_no) {
        return mApiService.confirmRenting(getToken(), order_no);
    }

    public Observable<DataInfo<OpenDoorListInfo<OpenDoorListbean>>> getOpenDoorlist(int p) {
        return mApiService.getOpendoorList(getToken(), p);
    }

    public Observable<DataInfo> checkPwd(String type, String password) {
        return mApiService.checkPwd(getToken(), type, password);
    }

    public Observable<DataInfo> addPwd(String type, String newPwd) {
        return mApiService.addPwd(getToken(), type, newPwd, "1");
    }

    public Observable<DataInfo> updatePwd(String type, String newPwd, String verify) {
        return mApiService.updatePwd(getToken(), type, newPwd, verify);
    }

    public Observable<DataInfo> updateLockState(String type, String is_verify) {
        return mApiService.updateLockState(getToken(), type, is_verify);
    }

    public Observable<DataInfo<ConfigInfoBean>> getConfigInfo() {
        return mApiService.getServiceConfig();
    }

    public Observable<DataInfo> landlordIdenty(String real_name, String identy) {
        return mApiService.landlordIdentification(getToken(), real_name, identy);
    }

    public Observable<DataInfo> rentIdenty(String real_name, String identy) {
        return mApiService.renterIdentification(getToken(), real_name, identy);
    }

    public Observable<DataInfo<CheckVersionBean>> checkVersion(String version) {
        return mApiService.checkVersion(version, "Android");
    }


    public Observable<DataInfo> deleteDevice(String id) {
        return mApiService.deleteDevice(getToken(), id);
    }

    public Observable<DataInfo> deleteGateway(String id) {
        return mApiService.deleteGateway(getToken(), id);
    }

    public Observable<DataInfo<CheckGatewayStatusBean>> queryGatewayStatus(String gatewayID) {
        return mApiService.queryGatewayStatus(getToken(), gatewayID);
    }


    public Observable<DataInfo> addContract(String order_no,String images) {
        return mApiService.addContract(getToken(), order_no,images);
    }

    public Observable<DataInfo<ContractBean>> getContract(String order_no) {
        return mApiService.getContract(getToken(), order_no);
    }

    public Observable<DataInfo> addDevicePassword(String device_id, String password,
                                                  int days) {
        return mApiService.addDevicePassword(getToken(), device_id,password,days);
    }

    public Observable<DataInfo<List<DevicesOpenPasswordBean>>> getDevicePasswordList(String device_id) {
        return mApiService.getDevicePasswordList(getToken(), device_id);
    }

    public Observable<DataInfo> delDevicePassword(String device_id,String password_id) {
        return mApiService.delDevicePassword(getToken(), device_id,password_id);
    }

    public Observable<DataInfo<List<String>>> rentingDate(String room_id) {
        return mApiService.rentingDate( room_id);
    }

    public Observable<DataInfo<AddRentingBean>> addRenting(String room_id, String start_time, String end_time, String mobile) {
        return mApiService.addRenting(getToken(), room_id,start_time,end_time,mobile);
    }
    public Observable<DataInfo<AddRentingBean>> addRenting(String room_id, String start_time, String end_time) {
        return mApiService.addRenting(getToken(), room_id,start_time,end_time);
    }

    public Observable<DataInfo<PwsOrderDetailsBean>> queryPassword(String room_id, String merge_order_no) {
        return mApiService.queryPassword(getToken(), room_id,merge_order_no);
    }

}
