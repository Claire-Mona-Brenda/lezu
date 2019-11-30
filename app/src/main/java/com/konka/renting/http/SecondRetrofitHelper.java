package com.konka.renting.http;

import android.app.ActivityManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.konka.renting.KonkaApplication;
import com.konka.renting.R;
import com.konka.renting.base.BaseActivity;
import com.konka.renting.base.BaseApplication;
import com.konka.renting.bean.ActivateBean;
import com.konka.renting.bean.AddBankInfo;
import com.konka.renting.bean.AddHouseBean;
import com.konka.renting.bean.AddRentingBean;
import com.konka.renting.bean.AgentBean;
import com.konka.renting.bean.AppConfigBean;
import com.konka.renting.bean.BannerListbean;
import com.konka.renting.bean.BillDetailBean;
import com.konka.renting.bean.BillListBean;
import com.konka.renting.bean.BillStatisticsBean;
import com.konka.renting.bean.BindGatewaySearchBean;
import com.konka.renting.bean.CheckGatewayStatusBean;
import com.konka.renting.bean.CheckWithdrawPwdBean;
import com.konka.renting.bean.CityBean;
import com.konka.renting.bean.CityInfo;
import com.konka.renting.bean.ClockSetManagerItemBean;
import com.konka.renting.bean.DataInfo;
import com.konka.renting.bean.DeviceHistoryBean;
import com.konka.renting.bean.DeviceInfo;
import com.konka.renting.bean.DevicesOpenPasswordBean;
import com.konka.renting.bean.GatewayDetailBean;
import com.konka.renting.bean.GatewayInfo;
import com.konka.renting.bean.GeneratePasswordBean;
import com.konka.renting.bean.GeneratePwdBean;
import com.konka.renting.bean.GetIssueBankBean;
import com.konka.renting.bean.GroupRoomListBean;
import com.konka.renting.bean.HomeInfo;
import com.konka.renting.bean.HouseConfigBean;
import com.konka.renting.bean.HouseDetailsInfoBean;
import com.konka.renting.bean.HouseDetailsInfoBean2;
import com.konka.renting.bean.HouseOrderInfoBean;
import com.konka.renting.bean.KInfo;
import com.konka.renting.bean.LandlordUserBean;
import com.konka.renting.bean.LandlordUserDetailsInfoBean;
import com.konka.renting.bean.ListInfo;
import com.konka.renting.bean.LoginUserBean;
import com.konka.renting.bean.MachineInfo;
import com.konka.renting.bean.MapLocationSearchBean;
import com.konka.renting.bean.MapSearchBean;
import com.konka.renting.bean.MessageListBean;
import com.konka.renting.bean.MoneyBean;
import com.konka.renting.bean.MyBankBean;
import com.konka.renting.bean.NativePwdBean;
import com.konka.renting.bean.OpenCityBean;
import com.konka.renting.bean.OpenDoorListbean;
import com.konka.renting.bean.OrderInfo;
import com.konka.renting.bean.PageDataBean;
import com.konka.renting.bean.PayBean;
import com.konka.renting.bean.PromotionCodeBean;
import com.konka.renting.bean.PwdBean;
import com.konka.renting.bean.PwsOrderDetailsBean;
import com.konka.renting.bean.QueryPwdBean;
import com.konka.renting.bean.ReminderListBean;
import com.konka.renting.bean.RentListBean;
import com.konka.renting.bean.RenterOrderInfoBean;
import com.konka.renting.bean.RenterOrderListBean;
import com.konka.renting.bean.RenterSearchListBean;
import com.konka.renting.bean.RentingDateBean;
import com.konka.renting.bean.RoomGroupListBean;
import com.konka.renting.bean.RoomInfo;
import com.konka.renting.bean.RoomOederPriceBean;
import com.konka.renting.bean.RoomPriceAreaBean;
import com.konka.renting.bean.RoomSearchInfoBean;
import com.konka.renting.bean.RoomTypeBean;
import com.konka.renting.bean.RoomTypeListBean;
import com.konka.renting.bean.RoomUserPhoneBean;
import com.konka.renting.bean.ServerDeviceInfo;
import com.konka.renting.bean.ServiceTelBean;
import com.konka.renting.bean.SeverPayListBean;
import com.konka.renting.bean.ShareRentListBean;
import com.konka.renting.bean.TenantListBean;
import com.konka.renting.bean.TenantRenterListBean;
import com.konka.renting.bean.TenantUserinfoBean;
import com.konka.renting.bean.UploadPicBean;
import com.konka.renting.bean.UserInfoBean;
import com.konka.renting.bean.UserProtocolBean;
import com.konka.renting.event.LogInAgainEvent;
import com.konka.renting.event.NoNetworkEvent;
import com.konka.renting.http.api.KonkaApiService;
import com.konka.renting.landlord.house.entity.DicEntity;
import com.konka.renting.landlord.house.widget.ShowToastUtil;
import com.konka.renting.login.LoginInfo;
import com.konka.renting.login.LoginNewActivity;
import com.konka.renting.utils.AppManager;
import com.konka.renting.utils.NetWorkUtil;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.UIUtils;
import com.lljjcoder.style.citylist.Toast.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

public class SecondRetrofitHelper {

    private static final String TAG = "SecondRetrofitHelper";
    public static final String PATH_DATA = BaseApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
    private static SecondRetrofitHelper mRetrofitHelper = null;
    private static Retrofit mRetrofit;
    private static KonkaApiService mApiService;

    private SecondRetrofitHelper() {

    }

    public static SecondRetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            synchronized (SecondRetrofitHelper.class) {
                if (mRetrofitHelper == null) mRetrofitHelper = new SecondRetrofitHelper();
                mRetrofit = createRetrofit(new Retrofit.Builder(), createOkHttpClient(new OkHttpClient.Builder()), KonkaApiService.SecondHost);
                mApiService = mRetrofit.create(KonkaApiService.class);
            }
        }

        return mRetrofitHelper;
    }

    private static OkHttpClient createOkHttpClient(OkHttpClient.Builder builder) {
        // HttpLoggingInterceptor 打印请求到的json字符串和查看log
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.addInterceptor(loggingInterceptor);

        File cacheFile = new File(SecondRetrofitHelper.PATH_CACHE); //缓存文件夹
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
                    RxBus.getDefault().post(new NoNetworkEvent());
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
//                Log.d(TAG, "--->返回报文，respString = " + respString);
                // TODO 这里判断是否是登录超时的情况
                JSONObject j = null;
                try {
                    j = new JSONObject(respString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 这里与后台约定的状态码700表示登录超时【后台是java，客户端自己维护cookie，没有token机制。但此处如果刷新token，方法也一样】
                if (j != null && j.optInt("status") == -1) {
                    RxBus.getDefault().post(new LogInAgainEvent());
//                    Log.e(TAG, "--->登录失效，自动重新登录");
//                    if (BaseActivity.getForegroundActivity() != null) {
                        LoginUserBean.getInstance().reset();
                        if (LoginUserBean.getInstance().isLandlord()) {
                            LoginNewActivity.toLandlordActivity(UIUtils.getContext());
                        } else {
                            LoginNewActivity.toTenantActivity(UIUtils.getContext());
                        }
//                        if (BaseActivity.getForegroundActivity() != null)
//                            BaseActivity.getForegroundActivity().finish();
//                        AppManager.getInstance().killAllBeyondActivity(LoginNewActivity.class);
//                    }
                    throw new RuntimeException("登录失效，请重新登录");
                    // 判断是否登录成功了
                }
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


    /********************************************************************新接口*******************************************************************************/
    /**
     * 添加设备
     */
    public Observable<DataInfo> addDevice(String room_id, String device_no, String type_id, String name) {
        return mApiService.addDevice(room_id, device_no, type_id, name);
    }

    /**
     * 开门
     */
    public Observable<DataInfo> openDoor(String room_id,
                                         String gateway_id,
                                         String device_id) {
        return mApiService.openDoor(room_id,
                gateway_id,
                device_id);
    }

    /**
     * 客服电话
     */
    public Observable<DataInfo<ServiceTelBean>> serviceTel() {
        return mApiService.serviceTel();
    }

    /**
     * 配置信息
     */
    public Observable<DataInfo<AppConfigBean>> appConfig(String type, String version) {
        return mApiService.appConfig(type, version);
    }

    /**
     * 首页轮播图
     */
    public Observable<DataInfo<List<BannerListbean>>> index() {
        return mApiService.index();
    }


    /**
     * 获取租客端开门列表
     */
    public Observable<DataInfo<List<OpenDoorListbean>>> getOpenRoomList() {
        return mApiService.getOpenRoomList();
    }

    /**
     * 获取房东首页用户信息
     */
    public Observable<DataInfo<LandlordUserBean>> getLandlordUserInfo() {
        return mApiService.getLandlordUserInfo();
    }

    /**
     * 获取房东用户信息
     */
    public Observable<DataInfo<LandlordUserDetailsInfoBean>> getLandlordUserDetailsInfo() {
        return mApiService.getLandlordUserDetailsInfo();
    }

    /**
     * 获取服务费缴费列表
     */
    public Observable<DataInfo<List<SeverPayListBean>>> serviceCharge() {
        return mApiService.serviceCharge();
    }

    /**
     * 服务费缴费
     */
    public Observable<DataInfo<PayBean>> serviceChargePay(String service_charge_id, String room_id, String payment) {
        return mApiService.serviceChargePay(service_charge_id, room_id, payment);
    }

    /**
     * 充值
     */
    public Observable<DataInfo<PayBean>> rechargePay(String money, String payment) {
        return mApiService.rechargePay(money, payment);
    }

    /**
     * 获取安装费金额
     */
    public Observable<DataInfo<MoneyBean>> installCharge() {
        return mApiService.installCharge();
    }

    /**
     * 安装费缴费
     */
    public Observable<DataInfo<PayBean>> installOrderPay(String room_id, String payment) {
        return mApiService.installOrderPay(room_id, payment);
    }

    /**
     * 申请删除房产
     */
    public Observable<DataInfo<PayBean>> serviceCharge(String room_id) {
        return mApiService.serviceCharge(room_id);
    }

    /**
     * 获取区域列表
     */
    public Observable<DataInfo<List<CityBean>>> getRegionList(String region_id, String type) {
        return mApiService.getRegionList(region_id, type);
    }

    /**
     * 获取开通城市列表
     */
    public Observable<DataInfo<List<OpenCityBean>>> getCityList() {
        return mApiService.getCityList();
    }

    /**
     * 上传图片
     */
    public Observable<DataInfo<UploadPicBean>> uploadPic(RequestBody fileName, MultipartBody.Part file) {
        return mApiService.uploadPic(fileName, file);
    }

    /**
     * 获取房产类型列表
     */
    public Observable<DataInfo<List<RoomTypeBean>>> getRoomTypeList() {
        return mApiService.getRoomTypeList();
    }

    /**
     * 获取代理商列表
     */
    public Observable<DataInfo<List<AgentBean>>> getAgentList(String city) {
        return mApiService.getAgentList(city);
    }

    /**
     * 获取房产配置列表
     */
    public Observable<DataInfo<List<HouseConfigBean>>> getRoomConfigList() {
        return mApiService.getRoomConfigList();
    }

    /**
     * 添加房产
     */
    public Observable<DataInfo> addRoom(String room_name,
                                        String room_type_id,
                                        String room_config_id,
                                        String agent_id,
                                        String province_id,
                                        String city_id,
                                        String area_id,
                                        String address,
                                        String total_floor,
                                        String floor,
                                        String measure_area,
                                        String remark,
                                        String explain,
                                        String image) {
        return mApiService.addRoom(room_name,
                room_type_id,
                room_config_id,
                agent_id,
                province_id,
                city_id,
                area_id,
                address,
                total_floor,
                floor,
                measure_area,
                remark,
                explain,
                image);
    }


    /**
     * 编辑房产
     */
    public Observable<DataInfo> editRoom(String room_id,
                                         String room_name,
                                         String room_type_id,
                                         String agent_id,
                                         String province_id,
                                         String city_id,
                                         String area_id,
                                         String address,
                                         String total_floor,
                                         String floor,
                                         String measure_area,
                                         String room_config_id,
                                         String remark,
                                         String explain,
                                         String image) {
        return mApiService.editRoom(room_id, room_name, room_type_id, agent_id, province_id, city_id, area_id, address, total_floor, floor, measure_area, room_config_id, remark, explain, image);
    }


    /**
     * 获取房东端房产列表
     */
    public Observable<DataInfo<PageDataBean<HouseOrderInfoBean>>> getRoomList(String page) {
        return mApiService.getRoomList(page);
    }

    /**
     * 获取房东端房产详情
     */
    public Observable<DataInfo<HouseDetailsInfoBean>> getHouseInfo(String room_id) {
        return mApiService.getHouseInfo(room_id);
    }


    /**
     * 申请删除房产
     */
    public Observable<DataInfo> applyDelHouse(String room_id) {
        return mApiService.applyDelHouse(room_id);
    }

    /**
     * 发布房产
     */
    public Observable<DataInfo> publishHouse(String room_id,//房产id
                                             String type,//1短租 2长租
                                             String housing_price,//租金
                                             String initial_water,//初始水表
                                             String initial_electric,//初始电表
                                             String water_rent,//水费
                                             String electric_rent,//电费
                                             String litter_rent,//垃圾处理费
                                             String property_rent,//物业费
                                             String cost_rent//网费
    ) {
        return mApiService.publishHouse(room_id,
                type,//1短租 2长租
                housing_price,//租金
                initial_water,//初始水表
                initial_electric,//初始电表
                water_rent,//水费
                electric_rent,//电费
                litter_rent,//垃圾处理费
                property_rent,//物业费
                cost_rent);
    }

    /**
     * 取消发布
     */
    public Observable<DataInfo> cancelPublishHouse(String room_id) {
        return mApiService.cancelPublishHouse(room_id);
    }

    /**
     * 房东申请短租
     */
    public Observable<DataInfo<AddRentingBean>> landlordAddShortApply(String room_id,
                                                                      String phone,
                                                                      String start_time,
                                                                      String end_time) {
        return mApiService.landlordAddShortApply(room_id,
                phone,
                start_time,
                end_time);
    }

    /**
     * 获取房东端短租账户申请信息
     */
    public Observable<DataInfo<PwsOrderDetailsBean>> landlordShortRentAccount(String order_id) {
        return mApiService.landlordShortRentAccount(order_id);
    }

    /**
     * 查询设置密码结果
     */
    public Observable<DataInfo<PwdBean>> queryPasswordResult(String password_id) {
        return mApiService.queryPasswordResult(password_id);
    }

    /**
     * 获取临时密码
     */
    public Observable<DataInfo<PwdBean>> sharePassword(String room_id) {
        return mApiService.sharePassword(room_id);
    }

    /**
     * 租客列表
     */
    public Observable<DataInfo<PageDataBean<TenantListBean>>> getRenterList(String page) {
        return mApiService.getRenterList(page);
    }

    /**
     * 账单列表
     */
    public Observable<DataInfo<PageDataBean<BillListBean>>> getAccountBillList(String page) {
        return mApiService.getAccountBillList(page);
    }

    /**
     * 获取账单详情
     */
    public Observable<DataInfo<BillDetailBean>> getBillDetail(String id) {
        return mApiService.getBillDetail(id);
    }

    /**
     * 催账房产列表
     */
    public Observable<DataInfo<PageDataBean<ReminderListBean>>> getReminderList(String page) {
        return mApiService.getReminderList(page);
    }

    /**
     * 设置催账
     */
    public Observable<DataInfo> reminderSet(String room_id, String day) {
        return mApiService.reminderSet(room_id, day);
    }

    /**
     * 关闭催账
     */
    public Observable<DataInfo> reminderClose(String room_id) {
        return mApiService.reminderClose(room_id);
    }

    /**
     * 获取消息列表
     */
    public Observable<DataInfo<PageDataBean<MessageListBean>>> getMessageList(String page) {
        return mApiService.getMessageList(page);
    }

    /**
     * 获取消息详情
     */
    public Observable<DataInfo<MessageListBean>> getMessageDetail(String id) {
        return mApiService.getMessageDetail(id);
    }

    /**
     * 获取房东确认订单
     */
    public Observable<DataInfo> landlordConfirm(String order_id) {
        return mApiService.landlordConfirm(order_id);
    }

    /**
     * 获取房东确认退房
     */
    public Observable<DataInfo> landlordConfirmCheckOut(String order_id) {
        return mApiService.landlordConfirmCheckOut(order_id);
    }

    /**
     * 取消退房
     */
    public Observable<DataInfo> cancelCheckOut(String order_id) {
        return mApiService.cancelCheckOut(order_id);
    }

    /**
     * 取消租房订单
     */
    public Observable<DataInfo> roomOrderCancel(String order_id) {
        return mApiService.roomOrderCancel(order_id);
    }

    /**
     * 获取租客端房产列表
     */
    public Observable<DataInfo<PageDataBean<RenterSearchListBean>>> getRenterRoomList(String page, String province, String city,
                                                                                      String area, String keyword, String rent_type, String type) {
        return mApiService.getRenterRoomList(page, province, city, area, keyword, rent_type, type);
    }

    /**
     * 获取租客端房产详情
     */
    public Observable<DataInfo<RoomSearchInfoBean>> getRenterRoomInfo(String room_id) {
        return mApiService.getRenterRoomInfo(room_id);
    }

    /**
     * 租客端申请租房
     */
    public Observable<DataInfo> memberAdd(String room_id,
                                          String verify,
                                          String start_time,
                                          String end_time,
                                          String remark) {
        return mApiService.memberAdd(room_id, verify, start_time, end_time, remark);
    }

    /**
     * 租客端申请续租
     */
    public Observable<DataInfo> relet(String order_id,
                                      String verify,
                                      String start_time,
                                      String end_time,
                                      String remark) {
        return mApiService.relet(order_id, verify, start_time, end_time, remark);
    }

    /**
     * 租客端申请退房
     */
    public Observable<DataInfo> checkOut(String room_id) {
        return mApiService.checkOut(room_id);
    }


    /**
     * 租客端申请合租
     */
    public Observable<DataInfo> jointRentApply(String room_id) {
        return mApiService.jointRentApply(room_id);
    }

    /**
     * 租客端同意合租申请
     */
    public Observable<DataInfo> jointRentApplyConfirm(String id, String order_id) {
        return mApiService.jointRentApplyConfirm(id, order_id);
    }

    /**
     * 租客端拒绝合租申请
     */
    public Observable<DataInfo> jointRentApplyCancel(String id, String order_id) {
        return mApiService.jointRentApplyCancel(id, order_id);
    }

    /**
     * 租客端移除合租订单
     */
    public Observable<DataInfo> jointRentApplyRemove(String id, String order_id) {
        return mApiService.jointRentApplyRemove(id, order_id);
    }

    /**
     * 租客端启用禁用合租发布
     */
    public Observable<DataInfo> setJointRentStatus(String order_id, String type) {
        return mApiService.setJointRentStatus(order_id, type);
    }

    /**
     * 获取租客端租客管理列表
     */
    public Observable<DataInfo<TenantRenterListBean>> getShareRentingList(String order_id) {
        return mApiService.getShareRentingList(order_id);
    }

    /**
     * 获取租客端订单列表
     */
    public Observable<DataInfo<PageDataBean<RenterOrderListBean>>> getRenterOrderList(String type, String page) {
        return mApiService.getOrderList(type, page);
    }

    /**
     * 获取租客端订单详情
     */
    public Observable<DataInfo<RenterOrderInfoBean>> getRenterOrderInfo(String order_id) {
        return mApiService.getOrderInfo(order_id);
    }

    /**
     * 获取房东端订单列表
     */
    public Observable<DataInfo<PageDataBean<RenterOrderListBean>>> getLandlordOrderList(String type, String page) {
        return mApiService.getOrderList(type, page);
    }

    /**
     * 获取房东端订单详情
     */
    public Observable<DataInfo<RenterOrderInfoBean>> getLandlordOrderInfo(String order_id) {
        return mApiService.getOrderInfo(order_id);
    }

    /**
     * 获取验证码
     */
    public Observable<DataInfo> getVerify(String mobile, String type) {
        return mApiService.getVerify(mobile, type);
    }

    /**
     * 获取短租查看开锁密码
     */
    public Observable<DataInfo<PwdBean>> shortRentOpenPassword(String order_id) {
        return mApiService.shortRentOpenPassword(order_id);
    }

    /**
     * 获取已租出日期
     */
    public Observable<DataInfo<List<String>>> rentingDate(String room_id) {
        return mApiService.rentingDate(room_id);
    }

    /************************************************用户*********************************************************/

    /**
     * 注册
     */
    public Observable<DataInfo> register(String mobile,
                                         String password,
                                         String verify) {
        return mApiService.register(mobile, password, verify);
    }

    /**
     * 登录
     */
    public Observable<DataInfo<LoginInfo>> login(String mobile,
                                                 String password,
                                                 String verify,
                                                 String method,
                                                 String type) {
        return mApiService.login(mobile, password, verify, method, type);
    }

    /**
     * 身份证认证
     */
    public Observable<DataInfo> identityAuth(String real_name,
                                             String identity,
                                             String identity_just,
                                             String identity_back,
                                             String photo,
                                             String sex,
                                             String birthday,
                                             String start_time,
                                             String end_time) {
        return mApiService.identityAuth(real_name, identity, identity_just, identity_back, photo, sex, birthday, start_time, end_time);
    }

    /**
     * 身份证号识别
     */
    public Observable<DataInfo> identityIdent(String real_name,
                                              String identity) {
        return mApiService.identityIdent(real_name, identity);
    }

    /**
     * 登录切换
     */
    public Observable<DataInfo> loginSwitch() {
        return mApiService.loginSwitch();
    }

    /**
     * 重置密码
     */
    public Observable<DataInfo> resetPassword(String mobile,
                                              String password,
                                              String verify) {
        return mApiService.resetPassword(mobile, password, verify);
    }

    /**
     * 手机号查询（是否查询）
     */
    public Observable<DataInfo> phoneCheck(String phone) {
        return mApiService.phoneCheck(phone);
    }


    /**
     * 修改绑定手机
     */
    public Observable<DataInfo> updateBindPhone(String mobile, String verify) {
        return mApiService.updateBindPhone(mobile, verify);
    }

    /**
     * 修改头像
     */
    public Observable<DataInfo> updateHead(String image) {
        return mApiService.updateHead(image);
    }

    /**
     * 用户协议
     */
    public Observable<DataInfo<UserProtocolBean>> userProtocol() {
        return mApiService.userProtocol();
    }

    /**************************************************网关设备*******************************************************************/
    /**
     * 绑定网关
     */
    public Observable<DataInfo> bindGateway(String room_id, String gateway_no, String gateway_pwd) {
        return mApiService.bindGateway(room_id, gateway_no, gateway_pwd);
    }

    /**
     * 解绑网关
     */
    public Observable<DataInfo> unbindGateway(String id) {
        return mApiService.unbindGateway(id);
    }

    /**
     * 查询网关信息
     */
    public Observable<DataInfo<BindGatewaySearchBean>> searchGateway(String gateway_no) {
        return mApiService.searchGateway(gateway_no);
    }

    /**
     * 网关列表
     */
    public Observable<DataInfo<List<GatewayInfo>>> gatewayList(String room_id) {
        return mApiService.gatewayList(room_id);
    }

    /**
     * 编辑网关
     */
    public Observable<DataInfo> editGateway(String id, String gateway_name) {
        return mApiService.editGateway(id, gateway_name);
    }

    /**
     * 网关详情
     */
    public Observable<DataInfo<GatewayDetailBean>> detailGateway(String id) {
        return mApiService.detailGateway(id);
    }

    /**
     * 重启网关
     */
    public Observable<DataInfo> reStart(String id) {
        return mApiService.reStart(id);
    }

    /**
     * 进入绑定模式
     */
    public Observable<DataInfo> gatewayBandMode(String id) {
        return mApiService.gatewayBandMode(id);
    }

    /**
     * 查询设备信息
     */
    public Observable<DataInfo<CheckGatewayStatusBean>> get_device_information(String id) {
        return mApiService.get_device_information(id);
    }

    /**
     * 查询设备信息
     */
    public Observable<DataInfo> setWifi(String room_id, String id, String wifi_name, String wifi_password) {
        return mApiService.setWifi(room_id, id, wifi_name, wifi_password);
    }

    /**
     * 绑定结果查询
     */
    public Observable<DataInfo<ServerDeviceInfo>> bindResult(String id) {
        return mApiService.bindResult(id);
    }

    /***********************************************门锁功能*************************************************************/


    /**
     * 应急密码
     */
    public Observable<DataInfo<NativePwdBean>> native_password(String room_id) {
        return mApiService.native_password(room_id);
    }

    /**
     * 录入指纹
     */
    public Observable<DataInfo> addFingerprint(String room_id) {
        return mApiService.addFingerprint(room_id);
    }

    /**
     * 绑定ic卡
     */
    public Observable<DataInfo> addIcCard(String room_id) {
        return mApiService.addIcCard(room_id);
    }

    /**
     * 指纹 ic卡 列表
     * type类型 1 遥控器 2 ic卡 3 指纹
     * page页码 默认1
     */
    public Observable<DataInfo<PageDataBean<ClockSetManagerItemBean>>> getFingerIcList(String room_id, String type, String page) {
        return mApiService.getFingerIcList(room_id, type, page);
    }

    /**
     * 指纹 ic卡 重命名
     */
    public Observable<DataInfo> renameFingerIc(String id, String name) {
        return mApiService.renameFingerIc(id, name);
    }

    /**
     * 指纹 ic卡 删除
     */
    public Observable<DataInfo> removeFingerIc(String id) {
        return mApiService.removeFingerIc(id);
    }

    /**
     * 查询 管理员密码 锁孔密码 临时密码
     * <p>
     * pwd_type 密码类型 0 管理员密码 1 临时密码 9 锁孔密码
     */
    public Observable<DataInfo<QueryPwdBean>> lockPwd(String room_id, String pwd_type) {
        return mApiService.lockPwd(room_id, pwd_type);
    }

    /**
     * 刷新 管理员密码 锁孔密码
     * <p>
     * pwd_type 密码类型 0 管理员密码  9 锁孔密码
     */
    public Observable<DataInfo<QueryPwdBean>> passwordRefresh(String room_id, String pwd_type) {
        return mApiService.passwordRefresh(room_id, pwd_type);
    }

    /**
     * 设置门锁密码
     * <p>
     * pwd_type 密码类型 1 临时密码 2普通密码    默认 2
     */
    public Observable<DataInfo<QueryPwdBean>> setPassword(String room_id, String device_id, String password, String hours, String pwd_type, String name) {
        return mApiService.setPassword(room_id, device_id, password, hours, pwd_type, name);
    }

    /**
     * 删除密码
     */
    public Observable<DataInfo> deletePassword(String device_id, String password_id) {
        return mApiService.deletePassword(device_id, password_id);
    }

    /**
     * 密码列表
     */
    public Observable<DataInfo<List<DevicesOpenPasswordBean>>> passwordList(String device_id) {
        return mApiService.passwordList(device_id);
    }

    /**
     * 获取管理员密码 发短信验证码
     */
    public Observable<DataInfo> getManageCode(String room_id, String mobile) {
        return mApiService.getManageCode(room_id, mobile);
    }

    /**
     * 获取管理员密码 确认
     */
    public Observable<DataInfo> getManagePwd(String room_id, String mobile, String code) {
        return mApiService.getManagePwd(room_id, mobile, code);
    }

    /**
     * 获取承租人手机号
     */
    public Observable<DataInfo<RoomUserPhoneBean>> room_used_phone(String room_id) {
        return mApiService.room_used_phone(room_id);
    }

    /***********************************************设备*************************************************************/

    /**
     * 获取设备类型列表
     */
    public Observable<DataInfo<PageDataBean<MachineInfo>>> getDeviceType(String page) {
        return mApiService.getDeviceType(page);
    }

    /**
     * 获取设备列表
     */
    public Observable<DataInfo<PageDataBean<DeviceInfo>>> getDeviceList(String room_id, String page) {
        return mApiService.getDeviceList(room_id, page);
    }

    /**
     * 同步服务费
     */
    public Observable<DataInfo> sync_service_expire(String room_id, String device_id) {
        return mApiService.sync_service_expire(room_id, device_id);
    }

    /*********************************************2.3.4版本以后新接口*****************************************************/
    /**
     * 添加房产(2.3.4版本以后)
     */
    public Observable<DataInfo<AddHouseBean>> addRoom2(String room_name, String room_type, String room_config_id,
                                                       String province, String city, String area, String map_address,
                                                       String address, String total_floor, String floor,
                                                       String measure_area, String remark, String image,
                                                       String lng, String lat) {
        return mApiService.addRoom2(room_name, room_type, room_config_id,
                province, city, area, map_address, address,
                total_floor, floor, measure_area,
                remark, image, lng, lat);
    }

    /**
     * 编辑房产(2.3.4版本以后)
     */
    public Observable<DataInfo> editRoom2(String room_id, String room_name, String room_type, String room_config_id,
                                          String province, String city, String area, String map_address, String address,
                                          String total_floor, String floor, String measure_area,
                                          String remark, String image, String lng, String lat) {
        return mApiService.editRoom2(room_id, room_name, room_type, room_config_id,
                province, city, area, map_address, address,
                total_floor, floor, measure_area, remark,
                image, lng, lat);
    }

    /**
     * 获取房东端房产列表(2.3.4版本以后)
     */
    public Observable<DataInfo<PageDataBean<HouseOrderInfoBean>>> getRoomList2(String page) {
        return mApiService.getRoomList2(page, "");
    }

    /**
     * 获取房东端房产详情(2.3.4版本以后)
     */
    public Observable<DataInfo<HouseDetailsInfoBean2>> getHouseInfo2(String room_id) {
        return mApiService.getHouseInfo2(room_id);
    }

    /**
     * 发布房产(2.3.4版本以后)
     */
    public Observable<DataInfo> publishHouse2(String room_id, String type, String housing_price) {
        return mApiService.publishHouse2(room_id, type, housing_price);
    }

    /**
     * 取消发布(2.3.4版本以后)
     */
    public Observable<DataInfo> cancelPublishHouse2(String room_id) {
        return mApiService.cancelPublishHouse2(room_id);
    }

    /**
     * 获取房产配置列表(2.3.4版本以后)
     */
    public Observable<DataInfo<List<HouseConfigBean>>> getRoomConfigList2() {
        return mApiService.getRoomConfigList2();
    }

    /**
     * 推广码查询(2.3.4版本以后)
     *
     * @code 推广码
     */
    public Observable<DataInfo<PromotionCodeBean>> promotionCodeDetail(String code) {
        return mApiService.promotionCodeDetail(code);
    }

    /**
     * 安装费支付(2.3.4版本以后)
     */
    public Observable<DataInfo<PayBean>> installOrderPay2(String payment, String room_id, String code_id, String service_charge_id) {
        return mApiService.installOrderPay2(payment, room_id, code_id, service_charge_id);
    }

    /*********************************************2.4.1版本以后新接口*****************************************************/
    /**
     * 添加订单(2.4.1版本以后)
     */
    public Observable<DataInfo<AddRentingBean>> addOrderPay2(String room_id, String phone, String start_time, String end_time) {
        return mApiService.addOrderPay2(room_id, phone, start_time, end_time);
    }

    /**
     * 获取已租出日期(2.4.1版本以后)
     */
    public Observable<DataInfo<List<RentingDateBean>>> rentingDate2(String room_id) {
        return mApiService.rentingDate2(room_id);
    }

    /**
     * 激活订单(2.4.1版本以后)
     */
    public Observable<DataInfo<ActivateBean>> activate(String account) {
        return mApiService.activate(account);
    }

    /**
     * 查询租客是否注册(2.4.1版本以后)
     */
    public Observable<DataInfo> check(String phone) {
        return mApiService.check(phone);
    }

    /**
     * 添加订单后调用详情(2.4.1版本以后)
     */
    public Observable<DataInfo<PwsOrderDetailsBean>> queryPassword2(String order_id) {
        return mApiService.queryPassword2(order_id);
    }

    /**
     * 订单列表(2.4.1版本以后)
     * <p>
     * type 1进行中 2完成
     */
    public Observable<DataInfo<PageDataBean<RenterOrderListBean>>> getOrderList2(String type, String page) {
        return mApiService.getOrderList2(type, page);
    }

    /**
     * 添加合租(2.4.1版本以后)
     */
    public Observable<DataInfo> addShareOrder(String order_id, String phone) {
        return mApiService.addShareOrder(order_id, phone);
    }

    /**
     * 移除合租人(2.4.1版本以后)
     */
    public Observable<DataInfo> removeShareOrder(String order_id, String member_id) {
        return mApiService.removeShareOrder(order_id, member_id);
    }

    /**
     * 租客列表(2.4.1版本以后)
     */
    public Observable<DataInfo<List<ShareRentListBean>>> shareList(String order_id) {
        return mApiService.shareList(order_id);
    }

    /**
     * 房屋租客列表(2.4.1版本以后)
     */
    public Observable<DataInfo<PageDataBean<RentListBean>>> renterList(String page, String room_id) {
        return mApiService.renterList(page, room_id);
    }

    /**
     * 取消订单(2.4.1版本以后)
     */
    public Observable<DataInfo> canceOrder(String order_id) {
        return mApiService.canceOrder(order_id);
    }

    /**
     * 删除房产(2.4.1版本以后)
     */
    public Observable<DataInfo> delHouse(String room_id) {
        return mApiService.delHouse(room_id);
    }

    /*****************************************************版本2.4.2**********************************************/
    /**
     * 添加小区(2.4.2版本以后)
     */
    public Observable<DataInfo<RoomGroupListBean>> roomGroupAdd(String name, String province, String city, String area, String business, String address, String lng, String lat) {
        return mApiService.roomGroupAdd(name, province, city, area, business, address, lng, lat);
    }

    /**
     * 小区列表(2.4.2版本以后)
     */
    public Observable<DataInfo<PageDataBean<RoomGroupListBean>>> roomGroupList(String page, String city_id, String keyword) {
        return mApiService.roomGroupList(page, city_id, keyword);
    }

    /**
     * 添加房产(2.4.2版本以后)
     */
    public Observable<DataInfo<AddHouseBean>> addRoom3(String room_name, String room_type, String room_config_id,
                                                       String room_group_id, String address, String total_floor,
                                                       String floor, String measure_area, String remark, String image) {
        return mApiService.addRoom3(room_name, room_type, room_config_id,
                room_group_id, address, total_floor, floor, measure_area,
                remark, image);
    }

    /**
     * 编辑房产(2.4.2版本以后)
     */
    public Observable<DataInfo> editRoom3(String room_id, String room_name, String room_type, String room_config_id,
                                          String room_group_id, String address, String total_floor, String floor,
                                          String measure_area, String remark, String image) {
        return mApiService.editRoom3(room_id, room_name, room_type, room_config_id,
                room_group_id, address, total_floor, floor, measure_area, remark,
                image);
    }

    /**
     * 价格区间(2.4.2版本以后)
     */
    public Observable<DataInfo<List<RoomPriceAreaBean>>> roomPriceArea(String rent_type) {
        return mApiService.roomPriceArea(rent_type);
    }

    /**
     * 房屋类型(2.4.2版本以后)
     */
    public Observable<DataInfo<List<RoomTypeListBean>>> roomTypeList() {
        return mApiService.roomTypeList();
    }

    /**
     * 地图找房(2.4.2版本以后)
     */
    public Observable<DataInfo<List<MapSearchBean>>> mapSearch(String level, String point1, String point2, String rent_type, String price_area_id, String room_type_id, String price_area, String keyword) {
        return mApiService.mapSearch(level, point1, point2, rent_type, price_area_id, room_type_id, price_area, keyword);
    }

    /**
     * 小区房屋列表(2.4.2版本以后)
     */
    public Observable<DataInfo<PageDataBean<GroupRoomListBean>>> groupRoomList(String page, String room_group_id, String rent_type, String price_area_id, String room_type_id, String price_area, String keyword) {
        return mApiService.groupRoomList(page, room_group_id, rent_type, price_area_id, room_type_id, price_area, keyword);
    }

    /**
     * 地图位置查询(2.4.2版本以后)
     */
    public Observable<DataInfo<List<MapLocationSearchBean>>> mapLocationSearch(String city, String rent_type, String keyword) {
        return mApiService.mapLocationSearch(city, rent_type, keyword);
    }

    /*********************************************版本2.4.3*************************************************/
    /**
     * 添加银行卡
     */
    public Observable<DataInfo> addBankCard(String card_no, String username,String phone) {
        return mApiService.addBankBean(card_no, username,phone);
    }

    /**
     * 银行卡验证
     */
    public Observable<DataInfo<GetIssueBankBean>> getIssueBank(String card_no) {
        return mApiService.getIssueBank(card_no);
    }

    /**
     * 银行卡列表
     */
    public Observable<DataInfo<PageDataBean<MyBankBean>>> getBankCardList(String page) {
        return mApiService.getBankCardList(page);
    }

    /**
     * 删除银行卡
     */
    public Observable<DataInfo> delBankBean(String card_id) {
        return mApiService.delBankBean(card_id);
    }

    /**
     * 设置提现密码 适用设置，修改，重置
     */
    public Observable<DataInfo> setWithdrawPassword(String code, String withdraw_password) {
        return mApiService.setWithdrawPassword(code, withdraw_password);
    }

    /**
     * 设置提现密码-密码验证
     */
    public Observable<DataInfo<CheckWithdrawPwdBean>> checkPassword(String type, String password) {
        return mApiService.checkPassword(type, password);
    }

    /**
     * 设置提现密码-手机验证码验证
     */
    public Observable<DataInfo<CheckWithdrawPwdBean>> checkVerify(String verify) {
        return mApiService.checkVerify(verify);
    }

    /**
     * 提现申请
     */
    public Observable<DataInfo> withdraw(String code, String card_id) {
        return mApiService.withdraw(code, card_id);
    }

    /**
     * 租客下单
     */
    public Observable<DataInfo<PayBean>> rentOrder(String room_id, String start_time, String end_time, String payment) {
        return mApiService.rentOrder(room_id, start_time, end_time, payment);
    }

    /**
     * 继续支付
     */
    public Observable<DataInfo<PayBean>> continueRentOrder(String order_id, String payment) {
        return mApiService.continueRentOrder(order_id, payment);
    }

    /**
     * 价格计算
     */
    public Observable<DataInfo<RoomOederPriceBean>> rentOrderPrice(String room_id, String start_time, String end_time) {
        return mApiService.rentOrderPrice(room_id, start_time, end_time);
    }

    /**
     * 开门记录
     */
    public Observable<DataInfo<PageDataBean<DeviceHistoryBean>>> openRoomRecord(String room_id, String page) {
        return mApiService.openRoomRecord(room_id, page);
    }

    /**
     * 账单列表
     */
    public Observable<DataInfo<PageDataBean<BillListBean>>> getAccountBillList2(String page,String type,String year,String month) {
        return mApiService.getAccountBillList2(page,type, year, month);
    }

    /**
     * 获取账单详情
     */
    public Observable<DataInfo<BillDetailBean>> getBillDetail2(String id) {
        return mApiService.getBillDetail2(id);
    }

    /**
     * 获取账单统计
     */
    public Observable<DataInfo<BillStatisticsBean>> getBillStatistics(String year, String month) {
        return mApiService.getBillStatistics(year, month);
    }

    /**
     * 计算密码-添加密码
     */
    public Observable<DataInfo<GeneratePwdBean>> addGeneratePassword(String type, String password,String device_id, String num, String start_time, String end_time) {
        return mApiService.addGeneratePassword(type,password, device_id,num,start_time,end_time);
    }

    /**
     * 计算密码-添加密码
     */
    public Observable<DataInfo> delGeneratePassword(String id) {
        return mApiService.delGeneratePassword(id);
    }

    /**
     * 计算密码-添加密码
     */
    public Observable<DataInfo<List<GeneratePasswordBean>>> getGeneratePasswordList() {
        return mApiService.getGeneratePasswordList();
    }
}
