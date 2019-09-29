package com.konka.renting.http.api;

import com.konka.renting.bean.*;
import com.konka.renting.landlord.house.entity.DicEntity;
import com.konka.renting.landlord.house.entity.RoomList;
import com.konka.renting.landlord.user.rentoutincome.RentOutIncomeBean;
import com.konka.renting.location.LocationInfo;
import com.konka.renting.login.LoginInfo;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author jzxiang
 * create at 7/6/17 23:16
 */
public interface KonkaApiService {

    //    //测试环境
    String HOST = "https://lettest.youlejiakeji.com/";
    String SecondHost = "https://lezuxiaowo-test.youlejiakeji.com";

    //正式环境
//    String HOST = "https://let.youlejiakeji.com/";
//    String SecondHost = "https://lezuxiaowo.youlejiakeji.com";


    @FormUrlEncoded
    @POST("index.php/Service/Login/landlordRegister")
    Observable<DataInfo<KInfo<LoginInfo>>> register_landlord(
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("verify") String verify);

    @FormUrlEncoded
    @POST("index.php/Service/Login/renterRegister")
    Observable<DataInfo<KInfo<LoginInfo>>> register_renter(
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("verify") String verify);


    /**
     * @param type      0租客1房东
     * @param mobile
     * @param isFindPwd 1.注册2.修改、重置密码3.修改绑定手机
     * @return
     */
    @GET("index.php/Service/Login/getVerify")
    Observable<DataInfo> sendVerCode(
            @Query("type") int type,
            @Query("mobile") String mobile,
            @Query("isFindPwd") int isFindPwd);

    @FormUrlEncoded
    @POST("index.php/Service/Login/renterRegisterVerify")
    Observable<DataInfo> verifyCodeRenter(@Field("mobile") String phone, @Field("verify") String verify);

    @FormUrlEncoded
    @POST("index.php/Service/Login/landlordRegisterVerify")
    Observable<DataInfo> verifyCodeLandLord(@Field("mobile") String phone, @Field("verify") String verify);

    @FormUrlEncoded
    @POST("index.php/Service/Login/landlordLogin")
    Observable<DataInfo<LoginInfo>> login_landlord(
            @Field("mobile") String mobile,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("index.php/Service/Login/renterLogin")
    Observable<DataInfo<LoginInfo>> login_renter(
            @Field("mobile") String mobile,
            @Field("password") String password);

    @GET("index.php/Service/LandlordMy/myInfo")
    Observable<DataInfo<UserInfoBean>> getUserInfo(@Query("token") String token);

    @FormUrlEncoded
    @POST("index.php/Service/Login/landlordLogout")
    Observable<DataInfo> landmoreLoginOut(@Field("token") String token);

    @GET("index.php/Service/RenterMy/myInfo")
    Observable<DataInfo<TenantUserinfoBean>> getTenantUserInfo(@Query("token") String token);

    @FormUrlEncoded
    @POST("index.php/Service/Login/renterLogout")
    Observable<DataInfo> tenantLoginout(@Field("token") String token);

    @GET("index.php/Service/Page/getLandlordIndexData")
    Observable<DataInfo<HomeInfo>> getHomeLandlordData(
            @Query("token") String token,
            @Query("city_id") String city_id,
            @Query("p") int page
    );

    @GET("index.php/Service/Page/getRenterIndexData")
    Observable<DataInfo<HomeInfo>> getHomeRenterData(
            @Query("token") String token,
            @Query("city_id") String city_id,
            @Query("p") int page
    );

    @GET("index.php/Service/LandlordRenter/getLandlordRoomList")
    Observable<DataInfo<ListInfo<RoomInfo>>> getLandlordRoomListData(
            @Query("token") String token,
            @Query("is_lease") String is_lease,
            @Query("p") int page
    );

    @GET("index.php/Service/Room/getLeaseType")
    Observable<DataInfo<DicEntity>> getLeaseType(
            @Query("token") String token);

    @GET("index.php/Service/RenterOrder/getLeaseType")
    Observable<DataInfo<DicEntity>> getRenterOrderLeaseType();

    @GET("index.php/Service/City/getAllLetterCity")
    Observable<DataInfo<DicEntity>> getAllLetterCity();

    @GET("index.php/Service/Room/getRoomType")
    Observable<DataInfo<DicEntity>> getRoomType(
            @Query("token") String token);

    @GET("index.php/Service/Room/getAgent")
    Observable<DataInfo<AgentBean>> getAgent();

    @GET("index.php/Service/Room/getOrientation")
    Observable<DataInfo<DicEntity>> getOrientation(
            @Query("token") String token);

    @GET("index.php/Service/Room/getConfig")
    Observable<DataInfo<DicEntity>> getConfig(
            @Query("token") String token);

    @GET("index.php/Service/LandlordRenter/getLandlordRoomList")
    Observable<DataInfo<RoomList>> getLandlordRoomList(
            @Query("token") String token);

    @GET("index.php/Service/Room/getRoomList")
    Observable<DataInfo<RoomList>> getRoomList(
            @Query("city_id") String city_id,
            @Query("lat") String lat,
            @Query("lng") String lng
    );

    @GET("index.php/Service/RenterOrder/getRoom")
    Observable<DataInfo<RoomInfo>> getRoom(
            @Query("token") String token,
            @Query("id") String city_id
    );

    @GET("index.php/Service/Config/getBond")
    Observable<DataInfo<HashMap>> getBond(
            @Query("province_id") String province_id,
            @Query("city_id") String city_id,
            @Query("area_id") String area_id
    );

    @GET("index.php/Service/LandlordRenter/getHistoryRoomList")
    Observable<DataInfo<RoomDes2>> getHistoryRoomList(
            @Query("token") String token,
            @Query("room_id") String room_id,
            @Query("p") int p

    );

    @FormUrlEncoded
    @POST("index.php/Service/Room/deleteRoom")
    Observable<DataInfo> deleteRoom(
            @Field("token") String token,
            @Field("id") String city_id
    );

    @GET("index.php/Service/Room/getRoomInfo")
    Observable<DataInfo<RoomInfo>> getRoomInfo(
            @Query("token") String token,
            @Query("id") String room_id
    );

    @FormUrlEncoded
    @POST("index.php/Service/Pay/boonOrderContinuedWechatPay")
    Observable<DataInfo<WxPayInfo>> boonOrderWechatPay(
            @Field("token") String token,
            @Field("room_id") String order_no);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/boonOrderContinuedBalancePay")
    Observable<DataInfo> boonOrderBalancePay(
            @Field("token") String token,
            @Field("room_id") String order_no);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/boonOrderContinuedAlipayPay")
    Observable<DataInfo<AlipayBean>> boonOrderAlipayPay(
            @Field("token") String token,
            @Field("room_id") String order_no);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/orderAlipayPay")
    Observable<DataInfo<AlipayBean>> orderAlipayPay(
            @Field("token") String token,
            @Field("order_no") String order_no);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/orderWechatPay")
    Observable<DataInfo<WxPayInfo>> orderWechatPay(
            @Field("token") String token,
            @Field("order_no") String order_no);

    @GET("index.php/Service/RenterOrder/getRenterRoomList")
    Observable<DataInfo<PayOrder>> getRenterRoomList(
            @Query("token") String token,
            @Query("status") String status,
            @Query("p") int p
    );

    @GET("index.php/Service/RenterOrder/getOpenRoomList")
    Observable<DataInfo<OpenDoorListInfo<OpenDoorListbean>>> getOpendoorList(@Query("token") String token,
                                                                             @Query("p") int p);

    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/updateRenting")
    Observable<DataInfo> updateRenting(
            @Field("token") String token,
            @Field("merge_order_no") String status,
            @Field("refund_reason") String refund_reason,
            @Field("state") String state
    );

    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/updateRentingMode")
    Observable<DataInfo> updateRentingMode(
            @Field("token") String token,
            @Field("renting_mode") String status,
            @Field("merge_order_no") String refund_reason
    );

    @GET("index.php/Service/RenterOrder/getHistoryOrderList")
    Observable<DataInfo<PayOrder>> getHistoryOrderList(
            @Query("token") String token,
            @Query("merge_order_no") String merge_order_no,
            @Query("p") int p
    );

    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/cancelCheckOut")
    Observable<DataInfo> cancelCheckOut(
            @Field("token") String token,
            @Field("merge_order_no") String merge_order_no
    );

    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/updateRoom")
    Observable<DataInfo> LandlordRenterupdateRoom(
            @Field("token") String token,
            @Field("room_id") String merge_order_no,
            @Field("is_on_sale") String is_on_sale
    );

    @FormUrlEncoded
    @POST("index.php/Service/LandlordMy/returnBond")
    Observable<DataInfo> LandlordMyreturnBond(
            @Field("token") String token,
            @Field("room_id") String merge_order_no
    );

    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/addApplyRenting")
    Observable<DataInfo> addApplyRenting(
            @Field("token") String token,
            @Field("room_id") String merge_order_no
    );

    @Multipart
    @POST("index.php/Service/Upload/uploadImage")
    Observable<DataInfo<Img>> uploadImage(
            @Part("file") RequestBody fullName,
            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("index.php/Service/RenterMy/addRoomComment")
    Observable<DataInfo> addRoomComment(
            @Field("token") String token,
            @Field("merge_order_no") String order_no,
            @Field("room_id") String room_id,
            @Field("content") String content,
            @Field("score") String score,
            @Field("is_anonymous") String is_anonymous,
            @Field("image_list") String image_list
    );

    @GET("index.php/Service/Room/getRoomCommentList")
    Observable<DataInfo<CommentTemp>> getRoomCommentList(
            @Query("room_id") String room_id,
            @Query("p") int p
    );

    @GET("index.php/Service/Login/getVerify")
    Observable<DataInfo> requestRoomgetVerify(

            @Query("type") String type,
            @Query("mobile") String mobile,
            @Query("isFindPwd") String isFindPwd
    );


    //    租凭类型ID（lease_type_id）：
//    房型ID（room_type_id）：
//    朝向ID（orientation_id）：
//    房价（housing_price）：
//    押金（deposit_price）：
//    面积（measure_area）：
//    说明（explain）：
//    省ID（province_id）：
//    城ID（city_id）：Auto
//    区ID（area_id）：
//    详细地址（address）：
//    经度（lat）：
//    纬度（lng）：
//    楼栋号（building_no）：
//    楼门号（door_no）：
//    总楼层（total_floor）：
//    楼层（floor）：
//    门牌号（door_number）：
//    房产图片（image）：
//    多张图片用逗号隔开，例如：1.jpg,2.jpg
//    初始水（initial_water）：
//    初始电（initial_electric）：
//    水费（water_rent）：
//    电费（electric_rent）：
//    垃圾处理费（litter_rent）：
//    物业费（property_rent）：
//    网费（cost_rent）：
//    公共配置ID（config_id）：
//    多个公共配用逗号隔开，例如：1,2,3
//    公共配置（config）：
    @FormUrlEncoded
    @POST("index.php/Service/Room/addRoom")
    Observable<DataInfo<RoomInfo>> addRoom(
            @Field("token") String token,
            @Field("gateway_account") String gateway_account,
            @Field("gateway_pwd") String gateway_pwd,
            @Field("room_name") String room_name,
            @Field("lease_type_id") String lease_type_id,
            @Field("room_type_id") String room_type_id,
            @Field("orientation_id") String orientation_id,
            @Field("housing_price") String housing_price,
            @Field("deposit_price") String deposit_price,
            @Field("measure_area") String measure_area,
            @Field("explain") String explain,
            @Field("province_id") String province_id,
            @Field("city_id") String city_id,
            @Field("area_id") String area_id,
            @Field("address") String address,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("building_no") String building_no,
            @Field("door_no") String door_no,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("door_number") String door_number,
            @Field("image") String image,
            @Field("initial_water") String initial_water,
            @Field("initial_electric") String initial_electric,
            @Field("water_rent") String water_rent,
            @Field("electric_rent") String electric_rent,
            @Field("litter_rent") String litter_rent,
            @Field("property_rent") String property_rent,
            @Field("cost_rent") String cost_rent,
            @Field("config_id") String config_id,
            @Field("config") String config,
            @Field("agent_id") String agent_id
    );

    @FormUrlEncoded
    @POST("index.php/Service/Room/perfectRoom")
    Observable<DataInfo<String>> perfectRoom(
            @Field("token") String token,
            @Field("type") String type,
            @Field("id") String id,
            @Field("housing_price") String housing_price,
            @Field("initial_water") String initial_water,
            @Field("initial_electric") String initial_electric,
            @Field("water_rent") String water_rent,
            @Field("electric_rent") String electric_rent,
            @Field("litter_rent") String litter_rent,
            @Field("property_rent") String property_rent,
            @Field("cost_rent") String cost_rent
    );

    @FormUrlEncoded
    @POST("index.php/Service/Room/perfectRoom")
    Observable<DataInfo<String>> perfectRoom(
            @Field("token") String token,
            @Field("type") String type,
            @Field("id") String id,
            @Field("housing_price") String housing_price,
            @Field("initial_water") String initial_water,
            @Field("initial_electric") String initial_electric,
            @Field("water_rent") String water_rent,
            @Field("electric_rent") String electric_rent,
            @Field("litter_rent") String litter_rent,
            @Field("property_rent") String property_rent,
            @Field("cost_rent") String cost_rent,
            @Field("server_pay_type") String server_pay_type
    );

    @FormUrlEncoded
    @POST("index.php/Service/Room/updateRoom")
    Observable<DataInfo<String>> updateRoom(
            @Field("token") String token,
            @Field("gateway_account") String gateway_account,
            @Field("gateway_pwd") String gateway_pwd,
            @Field("room_name") String room_name,
            @Field("id") String id,
            @Field("lease_type_id") String lease_type_id,
            @Field("room_type_id") String room_type_id,
            @Field("orientation_id") String orientation_id,
            @Field("housing_price") String housing_price,
            @Field("deposit_price") String deposit_price,
            @Field("measure_area") String measure_area,
            @Field("explain") String explain,
            @Field("province_id") String province_id,
            @Field("city_id") String city_id,
            @Field("area_id") String area_id,
            @Field("address") String address,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("building_no") String building_no,
            @Field("door_no") String door_no,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("door_number") String door_number,
            @Field("image") String image,
            @Field("initial_water") String initial_water,
            @Field("initial_electric") String initial_electric,
            @Field("water_rent") String water_rent,
            @Field("electric_rent") String electric_rent,
            @Field("litter_rent") String litter_rent,
            @Field("property_rent") String property_rent,
            @Field("cost_rent") String cost_rent,
            @Field("config_id") String config_id,
            @Field("config") String config,
            @Field("image_name") String image_name
    );


    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/addRenting")
    Observable<DataInfo> addRenting(
            @Field("token") String token,
            @Field("room_id") String room_id,
            @Field("start_time") String start_time,
            @Field("end_time") String end_time,
            @Field("rent_type_id") String rent_type_id,
            @Field("renting_mode") String renting_mode,
            @Field("housing_price") String housing_price,
            @Field("name") String name,
            @Field("tel") String tel,
            @Field("code") String code,
            @Field("identity") String identity,
            @Field("buyer_remark") String buyer_remark


    );

    @GET("index.php/Service/City/getHotCity")
    Observable<DataInfo<CityInfo>> getHotCity();

    /**
     * 是否已租出0否1是
     *
     * @param token
     * @param is_lease
     * @return
     */
    @GET("index.php/Service/LandlordRenter/getLandlordRoomList")
    Observable<DataInfo<ListInfo<RoomInfo>>> getLandlordRoomList(
            @Query("token") String token,
            @Query("is_lease") int is_lease,
            @Query("p") int page);

    @GET("index.php/Service/LandlordRenter/getSoonExpireRoomList")
    Observable<DataInfo<ListInfo<RoomInfo>>> getSoonExpireRoomList(
            @Query("token") String token,
            @Query("p") int page);

    @GET("index.php/Service/LandlordRenter/getHydropowerOrder")
    Observable<DataInfo<ListInfo<OrderInfo>>> getReadingList(
            @Query("token") String token,
            @Query("p") int page);

    @GET("index.php/Service/LandlordRenter/getStartHydropower")
    Observable<DataInfo<OrderInfo>> getReadingInfo(
            @Query("token") String token,
            @Query("id") String room_id
    );

    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/addHydropowerOrder")
    Observable<DataInfo> addHydropowerOrder(
            @Field("token") String token,
            @Field("merge_order_no") String order_no,
            @Field("end_electric") String end_electric,
            @Field("end_water") String end_water

    );

    /**
     * 账单列表
     *
     * @param token
     * @param state 0未出账1已出账
     * @param page
     * @return
     */
    @GET("index.php/Service/LandlordRenter/getOrder")
    Observable<DataInfo<ListInfo<OrderInfo>>> getOrder(
            @Query("token") String token,
            @Query("state") int state,
            @Query("p") int page);

    @GET("index.php/Service/QcloudImage/faceLiveGetFour")
    Observable<DataInfo<GetFaceVerfyBean>> getFaceVerfy(@Query("token") String token);

    @Multipart
    @POST("index.php/Service/Upload/uploadVideo")
    Observable<DataInfo<UploadRecordBean>> uploadRecord(@Part MultipartBody.Part record);

    @Multipart
    @POST("index.php/Service/Upload/uploadImage")
    Observable<DataInfo<UploadRecordBean>> uploadPhoto(
            @Part MultipartBody.Part photo);

    @Multipart
    @POST("index.php/Service/Upload/uploadImage")
    Observable<DataInfo<UploadRecordBean>> uploadPhotos(
            @Part() List<MultipartBody.Part> parts);

    @GET("index.php/Service/QcloudImage/faceLiveDetectFour")
    Observable<DataInfo> faceDetect(@Query("token") String token,
                                    @Query("validate_data") String verfy,
                                    @Query("video") String videoName);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordMy/updateMy")
    Observable<DataInfo> updateLandlordUserInfo(@Field("token") String token,
                                                @Field("headimgurl") String headimgurl,
                                                @Field("nickname") String real_name,
                                                @Field("sex") String sex,
                                                @Field("birthday") String birthday,
                                                @Field("tel") String tel,
                                                @Field("identity") String identity,
                                                @Field("identity_just") String identity_just,
                                                @Field("identity_back") String identity_back);

    @FormUrlEncoded
    @POST("index.php/Service/RenterMy/updateMy")
    Observable<DataInfo> updateRenterUserInfo(@Field("token") String token,
                                              @Field("headimgurl") String headimgurl,
                                              @Field("nickname") String real_name,
                                              @Field("sex") String sex,
                                              @Field("birthday") String birthday,
                                              @Field("tel") String tel,
                                              @Field("identity") String identity,
                                              @Field("identity_just") String identity_just,
                                              @Field("identity_back") String identity_back);

    @GET("index.php/Service/LandlordMy/memberIncomeLog")
    Observable<DataInfo<ListInfo<RentOutIncomeBean>>> getRentOutIncome(@Query("token") String token,
                                                                       @Query("p") int page);

    @GET("index.php/Service/LandlordRenter/getRenterRoomList")
    Observable<DataInfo<ListInfo<OrderBean>>> getLandmoreOrderList(@Query("token") String token,
                                                                   @Query("status") int status,
                                                                   @Query("p") int page);


    @GET("index.php/Service/Room/getRoomList")
    Observable<DataInfo<ListInfo<RoomInfo>>> searchRoomList(@Query("keyword") String keyword,
                                                            @Query("city_id") String city_id);

    @GET("index.php/Service/Room/getRoomList")
    Observable<DataInfo<ListInfo<RoomInfo>>> getMoreRoomList(@Query("p") int p,
                                                             @Query("city_id") String city_id);

    @FormUrlEncoded
    @POST("index.php/Service/Login/landlordResetPassword")
    Observable<DataInfo> resetPasswordLandlord(@Field("mobile") String mobile,
                                               @Field("newPwd") String newPwd,
                                               @Field("reNewPwd") String reNewPwd,
                                               @Field("verify") String verify);

    @FormUrlEncoded
    @POST("index.php/Service/Login/renterResetPassword")
    Observable<DataInfo> resetPasswordRenter(@Field("mobile") String mobile,
                                             @Field("newPwd") String newPwd,
                                             @Field("reNewPwd") String reNewPwd,
                                             @Field("verify") String verify);

    @GET("index.php/Service/City/location")
    Observable<DataInfo<KInfo<LocationInfo>>> location(@Query("city_name") String city_name);

    @GET("index.php/Service/LandlordRenter/updateCheckOut")
    Observable<DataInfo> updateCheckOut(@Query("token") String token,
                                        @Query("refund_image") String refund_image,
                                        @Query("status") int status,
                                        @Query("merge_order_no") String merge_order_no);

    @GET("index.php/Service/LandlordRenter/updateCheckOut")
    Observable<DataInfo> updateCheckOut(@Query("token") String token,
                                        @Query("status") int status,
                                        @Query("merge_order_no") String merge_order_no);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/updateRenting")
    Observable<DataInfo> updateRenting(@Field("token") String token,
                                       @Field("merge_order_no") String merge_order_no,
                                       @Field("state") int state);

    @GET("index.php/Service/LandlordRenter/getHistoryOrderList")
    Observable<DataInfo<OrderRentingHistoryBean>> getOrderRentingHistory(@Query("token") String token,
                                                                         @Query("merge_order_no") String merge_order_no,
                                                                         @Query("p") int page);

    @GET("index.php/Service/LandlordRenter/getLandlordRoomInfo")
    Observable<DataInfo<OrderDetailBean>> getLandlordOrderDetail(@Query("token") String token,
                                                                 @Query("merge_order_no") String orderNum);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/updateApplyRentingMode")
    Observable<DataInfo> changePayType(@Field("token") String token,
                                       @Field("merge_order_no") String orderNum,
                                       @Field("status") int status);


    @GET("index.php/Service/RenterOrder/getDepositOrderList")
    Observable<DataInfo<ListInfo<TenantDespoitlistBean>>> getTenantDespoit(@Query("token") String token,
                                                                           @Query("p") int page);

    @GET("index.php/Service/RenterOrder/getDepositOrderInfo")
    Observable<DataInfo<TenantDespoitDetailBean>> getTenantDespoitDetail(@Query("token") String token,
                                                                         @Query("merge_order_no") String merge_order_no,
                                                                         @Query("p") int page);

    @GET("index.php/Service/City/getCityLatLng")
    Observable<DataInfo<LocationInfo.LatLng>> getLatLng(@Query("id") String city_id);

    @GET("index.php/Service/LandlordRenter/getOrder")
    Observable<DataInfo<ListInfo<BillBean>>> getBillList(@Query("token") String token,
                                                         @Query("state") int status,
                                                         @Query("p") int page);

    @GET("index.php/Service/LandlordRenter/reminderRent")
    Observable<DataInfo> reminderRent(@Query("token") String token,
                                      @Query("order_no") String order_no);

    @GET("index.php/Service/LandlordRenter/getReminder")
    Observable<DataInfo<ListInfo<CollectionListBean>>> getReminder(@Query("token") String token,
                                                                   @Query("p") int page);


    @FormUrlEncoded
    @POST("index.php/Service/LandlordMy/applyWithdraw")
    Observable<DataInfo> applyWithdraw(@Field("token") String token,
                                       @Field("bank_id") String bank_id,
                                       @Field("amount") String amount);

    @GET("index.php/Service/LandlordMy/getWithdrawLog")
    Observable<DataInfo<ListInfo<WithDrawRecordBean>>> getWithDrawRecord(@Query("token") String token,
                                                                         @Query("p") int page);

    @GET("index.php/Service/LandlordMy/myIncome")
    Observable<DataInfo<ProfileBean>> getMyProfile(@Query("token") String token,
                                                   @Query("yearMonth") String yearMonth);

    @GET("index.php/Service/LandlordMessage/getLandlordMessageList")
    Observable<DataInfo<ListInfo<MessageBean>>> getMessageList(@Query("token") String token,
                                                               @Query("p") int page);


    @GET("index.php/Service/RenterMessage/getMessageInfo")
    Observable<DataInfo<MessageDetailBean>> getRenterMessageDetail(@Query("id") String id);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/updateApplyRenew")
    Observable<DataInfo> reNew(@Field("token") String token,
                               @Field("merge_order_no") String merge_order_no);

    @GET("index.php/Service/RenterMy/getRenterList")
    Observable<TenantManagerBean> getTenantManager(@Query("token") String token,
                                                   @Query("merge_order_no") String merge_order_no);

    @FormUrlEncoded
    @POST("index.php/Service/RenterMy/updateRenter")
    Observable<DataInfo> updateRenter(@Field("token") String token,
                                      @Field("id") String id,
                                      @Field("state") String state);

    @FormUrlEncoded
    @POST("index.php/Service/RenterMy/deleteRenter")
    Observable<DataInfo> deleteRenter(@Field("token") String token,
                                      @Field("id") String id);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordMy/updateBindTel")
    Observable<DataInfo> bindMobile(@Field("token") String token,
                                    @Field("mobile") String mobile,
                                    @Field("verify") String verify);

    @FormUrlEncoded
    @POST("index.php/Service/RenterMy/updateBindTel")
    Observable<DataInfo> renterbindMobile(@Field("token") String token,
                                          @Field("mobile") String mobile,
                                          @Field("verify") String verify);

    @GET("index.php/Service/RenterMessage/getRenterMessageList")
    Observable<DataInfo<ListInfo<MessageBean>>> getRenterMessageList(@Query("token") String token,
                                                                     @Query("p") int page);

    @GET("index.php/Service/RenterOrder/getRenterRoomInfo")
    Observable<DataInfo<TenantOrderDetailBean>> getTenantOrderDetail(@Query("token") String token,
                                                                     @Query("merge_order_no") String merge_order_no);

    @GET("index.php/Service/QcloudImage/frontIdcardDetect")
    Observable<DataInfo<IdCardFrontbean>> idCardFrontDectect(@Query("token") String token,
                                                             @Query("image") String image);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordMy/identityAuthentication")
    Observable<DataInfo> identityAuthentication(@Field("token") String token,
                                                @Field("real_name") String real_name,
                                                @Field("identity") String identity,
                                                @Field("identity_just") String identity_just,
                                                @Field("identity_back") String identity_back);

    @FormUrlEncoded
    @POST("index.php/Service/RenterMy/identityAuthentication")
    Observable<DataInfo> identityTentAuthentication(@Field("token") String token,
                                                    @Field("real_name") String real_name,
                                                    @Field("identity") String identity,
                                                    @Field("identity_just") String identity_just,
                                                    @Field("identity_back") String identity_back);

    @GET("index.php/Service/Config/getServiceCharge")
    Observable<DataInfo<ServicePackageBean>> getServicePackage();

    @FormUrlEncoded
    @POST("index.php/Service/Pay/serviceOrderWechatPay")
    Observable<DataInfo<WxPayInfo>> serviceOrderWechatPay(@Field("token") String token,
                                                          @Field("merge_order_no") String merge_order_no,
                                                          @Field("number") String number);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/serviceOrderAlipayPay")
    Observable<DataInfo<AlipayBean>> serviceOrderAliPay(@Field("token") String token,
                                                        @Field("merge_order_no") String merge_order_no,
                                                        @Field("number") String number);

    @GET("index.php/Service/LandlordMy/getRechargeOrder")
    Observable<DataInfo<ListInfo<RechargeBean>>> getRecharge(@Query("token") String token,
                                                             @Query("p") int page);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/rechargeOrderWechatPay")
    Observable<DataInfo<WxPayInfo>> rechargeOrderWechatPay(@Field("token") String token,
                                                           @Field("order_amount") String order_amount);

    @FormUrlEncoded
    @POST("index.php/Service/Pay/rechargeOrderAlipayPay")
    Observable<DataInfo<AlipayBean>> rechargeOrderAliPay(@Field("token") String token,
                                                         @Field("order_amount") String order_amount);

    @GET("index.php/Service/Config/getH5Url")
    Observable<DataInfo<WebUrlInfo>> getWebUrl();


//    @FormUrlEncoded
//    @POST("index.php/Service/Equipment/getEquipmentList")
//    Observable<DataInfo<ListInfo<DeviceInfo>>> getDeviceList(@Field("token") String token, @Field("room_id") String room_id);

    @GET("index.php/Service/Equipment/getEquipmentType")
    Observable<DataInfo<ListInfo<MachineInfo>>> getDeviceType();

    @FormUrlEncoded
    @POST("index.php/Service/Equipment/updateEquipment")
    Observable<DataInfo> editDevice(@Field("token") String token,
                                    @Field("equipment_id") String equipment_id,
                                    @Field("name") String name);

    @FormUrlEncoded
    @POST("index.php/Service/Equipment/addGateway")
    Observable<DataInfo> addGateway(@Field("token") String token,
                                    @Field("room_id") String room_id,
                                    @Field("gateway_name") String name,
                                    @Field("gateway_account") String code,
                                    @Field("gateway_pwd") String password,
                                    @Field("address") String address);


    @FormUrlEncoded
    @POST("index.php/Service/Equipment/getGatewayList")
    Observable<DataInfo<ListInfo<GatewayInfo>>> getGatewayList(@Field("token") String token,
                                                               @Field("room_id") String room_id);

    @FormUrlEncoded
    @POST("index.php/Service/Gateway433m/bindMode")
    Observable<DataInfo> goBandingMode(
            @Field("token") String token,
            @Field("gatewayID") String gateway_id
    );

    @FormUrlEncoded
    @POST("index.php/Service/Equipment/addEquipment")
    Observable<DataInfo<ServerDeviceInfo>> addDevice(
            @Field("token") String token,
            @Field("device_name") String device_id,
            @Field("gateway_id") String gateway_id,
            @Field("room_id") String room_id,
            @Field("type_id") String type,
            @Field("name") String name
//            @Field("server_device_id") String server_device_id
    );

    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/updateApplyRenew")
    Observable<DataInfo> reNew(@Field("token") String token,
                               @Field("merge_order_no") String merge_order_no,
                               @Field("apply_start_time") String apply_start_time,
                               @Field("apply_end_time") String apply_end_time);

    //    {"data":{"device_name":"02000008B500"},"status":1,"info":""}
    @FormUrlEncoded
    @POST("index.php/Service/Gateway433m/getBindDevice")
    Observable<DataInfo<ServerDeviceInfo>> checkBandingStatus(@Field("token") String token, @Field("gatewayID") String gateway_id);

    @GET("index.php/Service/Config/getShare")
    Observable<DataInfo<ShareBean>> getShareApp();

    @GET("index.php/Service/LandlordRenter/sharePassword")
    Observable<DataInfo<ShareBean>> sharePassword(@Query("token") String token,
                                                  @Query("room_id") String roomid);

    @GET("index.php/Service/RenterOrder/getGatewayPwd")
    Observable<DataInfo<String>> getGatewayPwd(@Query("token") String token,
                                               @Query("room_id") String room_id);

    @GET("index.php/Service/LandlordRenter/confirmRenting")
    Observable<DataInfo> confirmRenting(@Query("token") String token,
                                        @Query("order_no") String order_no);

    @GET("index.php/Service/Room/LockPasswordVerification")
    Observable<DataInfo> checkPwd(@Query("token") String token,
                                  @Query("type") String type,
                                  @Query("password") String password);

    @FormUrlEncoded
    @POST("index.php/Service/Room/updateLockPassword")
    Observable<DataInfo> updatePwd(@Field("token") String token,
                                   @Field("type") String type,
                                   @Field("newPwd") String newPwd,
                                   @Field("verify") String verify);

    @FormUrlEncoded
    @POST("index.php/Service/Room/updateLockPassword")
    Observable<DataInfo> addPwd(@Field("token") String token,
                                @Field("type") String type,
                                @Field("newPwd") String newPwd,
                                @Field("is_add") String is_add);

    @FormUrlEncoded
    @POST("index.php/Service/Room/updateVerification")
    Observable<DataInfo> updateLockState(@Field("token") String token,
                                         @Field("type") String type,
                                         @Field("is_verification") String is_verification);

    @GET("index.php/Service/config/getConfigInfo")
    Observable<DataInfo<ConfigInfoBean>> getServiceConfig();

    @GET("index.php/Service/LandlordMy/identityIdentification")
    Observable<DataInfo> landlordIdentification(@Query("token") String token,
                                                @Query("real_name") String real_name,
                                                @Query("identity") String identity);

    @GET("index.php/Service/RenterMy/identityIdentification")
    Observable<DataInfo> renterIdentification(@Query("token") String token,
                                              @Query("real_name") String real_name,
                                              @Query("identity") String identity);

    @GET("index.php/Service/Config/verifyingVersion")
    Observable<DataInfo<CheckVersionBean>> checkVersion(@Query("version") String version,
                                                        @Query("type") String type);

    @FormUrlEncoded
    @POST("index.php/Service/Equipment/deleteEquipment")
    Observable<DataInfo> deleteDevice(@Field("token") String token,
                                      @Field("id") String id);

    @FormUrlEncoded
    @POST("index.php/Service/Equipment/deleteGateway")
    Observable<DataInfo> deleteGateway(@Field("token") String token,
                                       @Field("id") String id);

    @FormUrlEncoded
    @POST("index.php/service/gateway433m/queryGatewayStatus")
    Observable<DataInfo<CheckGatewayStatusBean>> queryGatewayStatus(@Field("token") String token,
                                                                    @Field("gatewayID") String gatewayID);

    /**
     * 添加合同
     *
     * @order_no 订单编号
     * @images 合同照片url, 多张用，分开
     */
    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/addContract")
    Observable<DataInfo> addContract(@Field("token") String token,
                                     @Field("order_no") String order_no,
                                     @Field("images") String images);

    /**
     * 获取合同详情
     *
     * @order_no 订单编号
     */
    @FormUrlEncoded
    @POST("index.php/Service/RenterOrder/getContract")
    Observable<DataInfo<ContractBean>> getContract(@Field("token") String token,
                                                   @Field("order_no") String order_no);

    /**
     * 添加门锁密码
     *
     * @device_id 设备id
     * @password 密码 6位数字
     * @days 有效天数
     */
    @FormUrlEncoded
    @POST("index.php/Service/room/addDevicePassword")
    Observable<DataInfo> addDevicePassword(@Field("token") String token,
                                           @Field("device_id") String device_id,
                                           @Field("password") String password,
                                           @Field("days") int days);

    /**
     * 获取门锁密码列表
     *
     * @device_id 设备id
     */
    @FormUrlEncoded
    @POST("index.php/Service/room/getDevicePasswordList")
    Observable<DataInfo<List<DevicesOpenPasswordBean>>> getDevicePasswordList(@Field("token") String token,
                                                                              @Field("device_id") String device_id);

    /**
     * 删除门锁密码
     *
     * @device_id 设备id
     * @password_id 密码id
     */
    @FormUrlEncoded
    @POST("index.php/Service/room/delDevicePassword")
    Observable<DataInfo> delDevicePassword(@Field("token") String token,
                                           @Field("device_id") String device_id,
                                           @Field("password_id") String password_id);


    /**
     * 房东端生成订单
     *
     * @room_id 房产id
     * @start_time 入住时间
     * @end_time 退租时间
     * @mobile 租客手机号
     */
    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/addRenting")
    Observable<DataInfo<AddRentingBean>> addRenting(@Field("token") String token,
                                                    @Field("room_id") String room_id,
                                                    @Field("start_time") String start_time,
                                                    @Field("end_time") String end_time,
                                                    @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/addRenting")
    Observable<DataInfo<AddRentingBean>> addRenting(@Field("token") String token,
                                                    @Field("room_id") String room_id,
                                                    @Field("start_time") String start_time,
                                                    @Field("end_time") String end_time);

    /**
     * 房东端查看生成订单的密码
     *
     * @room_id 房产id
     * @merge_order_no 订单编号
     */
    @FormUrlEncoded
    @POST("index.php/Service/LandlordRenter/queryPassword")
    Observable<DataInfo<PwsOrderDetailsBean>> queryPassword(@Field("token") String token,
                                                            @Field("room_id") String room_id,
                                                            @Field("merge_order_no") String merge_order_no);


    /********************************************************************新接口*******************************************************************************/
    /**
     * 添加设备
     */
    @FormUrlEncoded
    @POST("index.php/api/device/addDevice")
    Observable<DataInfo> addDevice(@Field("room_id") String room_id,
                                   @Field("device_no") String device_no,
                                   @Field("type_id") String type_id,
                                   @Field("name") String name);

    /**
     * 开门
     */
    @FormUrlEncoded
    @POST("index.php/api/room/openDoor")
    Observable<DataInfo> openDoor(@Field("room_id") String room_id,
                                  @Field("gateway_id") String gateway_id,
                                  @Field("device_id") String device_id);

    /**
     * 客服电话
     */
    @GET("index.php/api/index/service_tel")
    Observable<DataInfo<ServiceTelBean>> serviceTel();

    /**
     * 配置信息
     * <p>
     * 1安卓 2苹果
     */
    @GET("index.php/api/index/appConfig")
    Observable<DataInfo<AppConfigBean>> appConfig(@Query("type") String type,
                                                  @Query("version") String version);

    /**
     * 首页轮播图
     */
    @GET("index.php/api/index/index")
    Observable<DataInfo<List<BannerListbean>>> index();

    /**
     * 获取首页用户信息
     */
    @GET("index.php/api/user/index")
    Observable<DataInfo<LandlordUserBean>> getLandlordUserInfo();

    /**
     * 获取用户信息
     */
    @GET("index.php/api/user/info")
    Observable<DataInfo<LandlordUserDetailsInfoBean>> getLandlordUserDetailsInfo();

    /**
     * 获取服务费缴费列表
     */
    @GET("index.php/api/fee/serviceCharge")
    Observable<DataInfo<List<SeverPayListBean>>> serviceCharge();

    /**
     * 服务费缴费
     *
     * @service_charge_id
     * @room_id
     * @payment 1微信 2支付宝 3余额
     */
    @FormUrlEncoded
    @POST("index.php/api/pay/serviceChargePay")
    Observable<DataInfo<PayBean>> serviceChargePay(@Field("service_charge_id") String service_charge_id,
                                                   @Field("room_id") String room_id,
                                                   @Field("payment") String payment);

    /**
     * 钱包充值缴费
     *
     * @money 充值金额
     * @payment 1微信 2支付宝 3余额
     */
    @FormUrlEncoded
    @POST("index.php/api/pay/rechargePay")
    Observable<DataInfo<PayBean>> rechargePay(@Field("money") String money,
                                              @Field("payment") String payment);

    /**
     * 获取安装费金额
     */
    @GET("index.php/api/fee/installCharge")
    Observable<DataInfo<MoneyBean>> installCharge();

    /**
     * 安装费缴费
     *
     * @room_id
     * @payment 1微信 2支付宝 3余额
     */
    @FormUrlEncoded
    @POST("index.php/api/pay/installOrderPay")
    Observable<DataInfo<PayBean>> installOrderPay(@Field("room_id") String room_id,
                                                  @Field("payment") String payment);

    /**
     * 申请退款
     *
     * @room_id
     */
    @FormUrlEncoded
    @POST("index.php/api/refund/serviceCharge")
    Observable<DataInfo<PayBean>> serviceCharge(@Field("room_id") String room_id);

    /**
     * 获取区域列表
     *
     * @region_id 默认1获取全部省份
     * @type 默认1 省 2市 3区
     */
    @GET("index.php/api/region/getRegionList")
    Observable<DataInfo<List<CityBean>>> getRegionList(@Query("region_id") String region_id,
                                                       @Query("type") String type);

    /**
     * 获取开通城市列表
     */
    @GET("index.php/api/region/getCityList")
    Observable<DataInfo<List<OpenCityBean>>> getCityList();

    /**
     * 上传图片
     */
    @Multipart
    @POST("index.php/api/upload/image")
    Observable<DataInfo<UploadPicBean>> uploadPic(
            @Part("file") RequestBody fileName,
            @Part MultipartBody.Part file);

    /**
     * 获取房产类型列表
     */
    @GET("index.php/api/room/roomType")
    Observable<DataInfo<List<RoomTypeBean>>> getRoomTypeList();

    /**
     * 获取代理商列表
     */
    @GET("index.php/api/room/agent")
    Observable<DataInfo<List<AgentBean>>> getAgentList(@Query("city") String city);

    /**
     * 获取房产配置列表
     */
    @GET("index.php/api/room/roomConfig")
    Observable<DataInfo<List<HouseConfigBean>>> getRoomConfigList();

    /**
     * 申请添加房产
     */
    @FormUrlEncoded
    @POST("index.php/api/room/add")
    Observable<DataInfo> addRoom(
            @Field("room_name") String room_name,
            @Field("room_type_id") String room_type_id,
            @Field("room_config_id") String room_config_id,
            @Field("agent_id") String agent_id,
            @Field("province_id") String province_id,
            @Field("city_id") String city_id,
            @Field("area_id") String area_id,
            @Field("address") String address,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("measure_area") String measure_area,
            @Field("remark") String remark,
            @Field("explain") String explain,
            @Field("image") String image);


    /**
     * 获取房东端房产列表
     *
     * @page 页数 默认1
     */
    @GET("index.php/api/room/index")
    Observable<DataInfo<PageDataBean<HouseOrderInfoBean>>> getRoomList(@Query("page") String page);


    /**
     * 房东端房产详情
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/info")
    Observable<DataInfo<HouseDetailsInfoBean>> getHouseInfo(@Field("room_id") String room_id);


    /**
     * 申请删除房产
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/applyDel")
    Observable<DataInfo> applyDelHouse(@Field("room_id") String room_id);

    /**
     * 编辑房产
     */
    @FormUrlEncoded
    @POST("index.php/api/room/edit")
    Observable<DataInfo> editRoom(
            @Field("room_id") String room_id,
            @Field("room_name") String room_name,
            @Field("room_type_id") String room_type_id,
            @Field("agent_id") String agent_id,
            @Field("province_id") String province_id,
            @Field("city_id") String city_id,
            @Field("area_id") String area_id,
            @Field("address") String address,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("measure_area") String measure_area,
            @Field("room_config_id") String room_config_id,
            @Field("remark") String remark,
            @Field("explain") String explain,
            @Field("image") String image);


    /**
     * 发布房产
     */
    @FormUrlEncoded
    @POST("index.php/api/room/publish")
    Observable<DataInfo> publishHouse(
            @Field("room_id") String room_id,//房产id
            @Field("type") String type,//1短租 2长租
            @Field("housing_price") String housing_price,//租金
            @Field("initial_water") String initial_water,//初始水表
            @Field("initial_electric") String initial_electric,//初始电表
            @Field("water_rent") String water_rent,//水费
            @Field("electric_rent") String electric_rent,//电费
            @Field("litter_rent") String litter_rent,//垃圾处理费
            @Field("property_rent") String property_rent,//物业费
            @Field("cost_rent") String cost_rent//网费
    );

    /**
     * 取消发布
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/cancel")
    Observable<DataInfo> cancelPublishHouse(@Field("room_id") String room_id);


    /**
     * 房东申请短租
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/landlordAdd")
    Observable<DataInfo<AddRentingBean>> landlordAddShortApply(@Field("room_id") String room_id,
                                                               @Field("phone") String phone,
                                                               @Field("start_time") String start_time,
                                                               @Field("end_time") String end_time);

    /**
     * 获取房东端短租账户申请信息
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/LandlordShortRentAccount")
    Observable<DataInfo<PwsOrderDetailsBean>> landlordShortRentAccount(@Field("order_id") String order_id);


    /**
     * 查询设置密码结果
     *
     * @password_id 密码id
     */
    @FormUrlEncoded
    @POST("index.php/api/device/queryPasswordResult")
    Observable<DataInfo<PwdBean>> queryPasswordResult(@Field("password_id") String password_id);

    /**
     * 获取临时密码
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/sharePassword")
    Observable<DataInfo<PwdBean>> sharePassword(@Field("room_id") String room_id);

    /**
     * 获取租客列表
     */
    @GET("index.php/api/landlord_center/renterList")
    Observable<DataInfo<PageDataBean<TenantListBean>>> getRenterList(@Query("page") String page);

    /**
     * 获取账单列表
     */
    @GET("index.php/api/landlord_center/accountBill")
    Observable<DataInfo<PageDataBean<BillListBean>>> getAccountBillList(@Query("page") String page);

    /**
     * 获取账单详情
     */
    @FormUrlEncoded
    @POST("index.php/api/landlord_center/billDetail")
    Observable<DataInfo<BillDetailBean>> getBillDetail(@Field("id") String id);


    /**
     * 设置催账
     */
    @FormUrlEncoded
    @POST("index.php/api/landlord_center/reminderSet")
    Observable<DataInfo> reminderSet(@Field("room_id") String room_id,
                                     @Field("day") String day);

    /**
     * 关闭催账
     */
    @FormUrlEncoded
    @POST("index.php/api/landlord_center/reminderClose")
    Observable<DataInfo> reminderClose(@Field("room_id") String room_id);

    /**
     * 获取消息列表
     */
    @GET("index.php/api/landlord_center/messageList")
    Observable<DataInfo<PageDataBean<MessageListBean>>> getMessageList(@Query("page") String page);

    /**
     * 获取消息详情
     */
    @FormUrlEncoded
    @POST("index.php/api/landlord_center/messageDetail")
    Observable<DataInfo<MessageListBean>> getMessageDetail(@Field("id") String id);


    /**
     * 获取房东确认订单
     *
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/confirm")
    Observable<DataInfo> landlordConfirm(@Field("order_id") String order_id);


    /**
     * 房东确认退房
     *
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/confirmCheckOut")
    Observable<DataInfo> landlordConfirmCheckOut(@Field("order_id") String order_id);

    /**
     * 取消退房
     *
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/cancelCheckOut")
    Observable<DataInfo> cancelCheckOut(@Field("order_id") String order_id);

    /**
     * 获取取消订单
     *
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/cancel")
    Observable<DataInfo> roomOrderCancel(@Field("order_id") String order_id);

    /**
     * 获取租客端房产列表
     * <p>
     * rent_type  0 默认 1 短租 2长租
     * type   0 默认 1推荐 2精品 3热门
     */
    @GET("index.php/api/room/renterRoomList")
    Observable<DataInfo<PageDataBean<RenterSearchListBean>>> getRenterRoomList(
            @Query("page") String page,
            @Query("province") String province,
            @Query("city") String city,
            @Query("area") String area,
            @Query("keyword") String keyword,
            @Query("rent_type") String rent_type,
            @Query("type") String type
    );

    /**
     * 租客端房产详情
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/renterRoomInfo")
    Observable<DataInfo<RoomSearchInfoBean>> getRenterRoomInfo(@Field("room_id") String room_id);

    /**
     * 租客端申请租房
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/memberAdd")
    Observable<DataInfo> memberAdd(@Field("room_id") String room_id,
                                   @Field("verify") String verify,
                                   @Field("start_time") String start_time,
                                   @Field("end_time") String end_time,
                                   @Field("remark") String remark);

    /**
     * 租客端申请续租
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/relet")
    Observable<DataInfo> relet(@Field("order_id") String order_id,
                               @Field("verify") String verify,
                               @Field("start_time") String start_time,
                               @Field("end_time") String end_time,
                               @Field("remark") String remark);

    /**
     * 租客端申请退房
     *
     * @order_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/checkOut")
    Observable<DataInfo> checkOut(@Field("order_id") String order_id);

    /**
     * 租客端申请合租
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/apply")
    Observable<DataInfo> jointRentApply(@Field("room_id") String room_id);

    /**
     * 租客端同意合租申请
     *
     * @id 合租订单id
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/confirm")
    Observable<DataInfo> jointRentApplyConfirm(@Field("id") String id,
                                               @Field("order_id") String order_id);

    /**
     * 租客端拒绝合租申请
     *
     * @id 合租订单id
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/cancel")
    Observable<DataInfo> jointRentApplyCancel(@Field("id") String id,
                                              @Field("order_id") String order_id);

    /**
     * 租客端移除合租订单
     *
     * @id 合租订单id
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/remove")
    Observable<DataInfo> jointRentApplyRemove(@Field("id") String id,
                                              @Field("order_id") String order_id);


    /**
     * 租客端启用禁用合租发布
     *
     * @type 1 启用 0禁用
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/status")
    Observable<DataInfo> setJointRentStatus(@Field("order_id") String order_id,
                                            @Field("type") String type);


    /**
     * 获取租客端房产列表
     */
    @GET("index.php/api/landlord_center/reminderList")
    Observable<DataInfo<PageDataBean<ReminderListBean>>> getReminderList(@Query("page") String page);


    /**
     * 获取订单列表
     * <p>
     * type 必选 0全部 1申请中 2入住中 3退房中 4过期
     */
    @GET("index.php/api/room_order/index")
    Observable<DataInfo<PageDataBean<RenterOrderListBean>>> getOrderList(@Query("type") String type,
                                                                         @Query("page") String page);


    /**
     * 获取订单详情
     *
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/info")
    Observable<DataInfo<RenterOrderInfoBean>> getOrderInfo(@Field("order_id") String order_id);

    /**
     * 获取租客端租客管理列表
     */
    @GET("index.php/api/room_share_order/renterList")
    Observable<DataInfo<TenantRenterListBean>> getShareRentingList(@Query("order_id") String order_id);


    /**
     * 获取租客端开门列表
     */
    @GET("index.php/api/room_order/openRoomList")
    Observable<DataInfo<List<OpenDoorListbean>>> getOpenRoomList();

    /**
     * 获取短租查看开锁密码
     *
     * @order_id 订单id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/shortRentPassword")
    Observable<DataInfo<PwdBean>> shortRentOpenPassword(@Field("order_id") String order_id);

    /**
     * 获取短租不能出租日期
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/rentingDate")
    Observable<DataInfo<List<String>>> rentingDate(@Field("room_id") String room_id);

/************************************************用户*********************************************************/
    /**
     * 获取验证码
     * <p>
     * 用途 1默认 2注册，修改绑定手机 3验证码登陆
     */
    @FormUrlEncoded
    @POST("index.php/api/index/getVerify")
    Observable<DataInfo> getVerify(@Field("mobile") String mobile,
                                   @Field("type") String type);

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("index.php/api/user/register")
    Observable<DataInfo> register(@Field("mobile") String mobile,
                                  @Field("password") String password,
                                  @Field("verify") String verify);

    /**
     * 登录
     *
     * @method 1密码登录 2短信登录
     * @type 1租客 0房东
     */
    @FormUrlEncoded
    @POST("index.php/api/user/login")
    Observable<DataInfo<LoginInfo>> login(@Field("mobile") String mobile,
                                          @Field("password") String password,
                                          @Field("verify") String verify,
                                          @Field("method") String method,
                                          @Field("type") String type);

    /**
     * 身份证认证
     *
     * @sex 性别 1男 2女
     * @birthday 生日 1999-01-01
     */
    @FormUrlEncoded
    @POST("index.php/api/user/identityAuth")
    Observable<DataInfo> identityAuth(@Field("real_name") String real_name,
                                      @Field("identity") String identity,
                                      @Field("identity_just") String identity_just,
                                      @Field("identity_back") String identity_back,
                                      @Field("photo") String photo,
                                      @Field("sex") String sex,
                                      @Field("birthday") String birthday,
                                      @Field("start_time") String start_time,
                                      @Field("end_time") String end_time);

    /**
     * 身份证号识别
     */
    @FormUrlEncoded
    @POST("index.php/api/user/identityIdent")
    Observable<DataInfo> identityIdent(@Field("real_name") String real_name,
                                       @Field("identity") String identity);

    /**
     * 登录切换
     */
    @GET("index.php/api/user/loginSwitch")
    Observable<DataInfo> loginSwitch();

    /**
     * 重置密码
     */
    @FormUrlEncoded
    @POST("index.php/api/user/resetPassword")
    Observable<DataInfo> resetPassword(@Field("mobile") String mobile,
                                       @Field("password") String password,
                                       @Field("verify") String verify);

    /**
     * 手机号查询（是否查询）
     */
    @FormUrlEncoded
    @POST("index.php/api/user/phoneCheck")
    Observable<DataInfo> phoneCheck(@Field("phone") String phone);

    /**
     * 修改绑定手机
     */
    @FormUrlEncoded
    @POST("index.php/api/user/updateBindPhone")
    Observable<DataInfo> updateBindPhone(@Field("mobile") String mobile,
                                         @Field("verify") String verify);

    /**
     * 修改头像
     */
    @FormUrlEncoded
    @POST("index.php/api/user/updateHead")
    Observable<DataInfo> updateHead(@Field("image") String image);

    /**
     * 用户协议
     */
    @GET("index.php/api/index/userProtocol")
    Observable<DataInfo<UserProtocolBean>> userProtocol();

    /************************************************网关设备************************************************************************/
    /**
     * 绑定网关
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/bind_gateway")
    Observable<DataInfo> bindGateway(@Field("room_id") String room_id,
                                     @Field("gateway_no") String gateway_no,
                                     @Field("gateway_pwd") String gateway_pwd);

    /**
     * 解绑网关
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/unbind_gateway")
    Observable<DataInfo> unbindGateway(@Field("id") String id);

    /**
     * 查询网关信息
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/search")
    Observable<DataInfo<BindGatewaySearchBean>> searchGateway(@Field("gateway_no") String gateway_no);

    /**
     * 网关列表
     */
    @GET("index.php/api/gateway/index")
    Observable<DataInfo<List<GatewayInfo>>> gatewayList(@Query("room_id") String room_id);

    /**
     * 编辑网关
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/edit")
    Observable<DataInfo> editGateway(@Field("id") String id,
                                     @Field("gateway_name") String gateway_name);

    /**
     * 网关详情
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/detail")
    Observable<DataInfo<GatewayDetailBean>> detailGateway(@Field("id") String id);

    /**
     * 重启网关
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/reboot")
    Observable<DataInfo> reStart(@Field("id") String id);

    /**
     * 进入绑定模式
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/band_mode")
    Observable<DataInfo> gatewayBandMode(@Field("id") String id);

    /**
     * 查询设备信息
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/get_device_information")
    Observable<DataInfo<CheckGatewayStatusBean>> get_device_information(@Field("id") String id);

    /**
     * 设置wifi
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/set_wifi")
    Observable<DataInfo> setWifi(@Field("room_id") String room_id,
                                 @Field("id") String id,
                                 @Field("wifi_name") String wifi_name,
                                 @Field("wifi_password") String wifi_password);

    /**
     * 绑定结果查询
     */
    @FormUrlEncoded
    @POST("index.php/api/gateway/bind_result")
    Observable<DataInfo<ServerDeviceInfo>> bindResult(@Field("id") String id);

    /***********************************************门锁功能*************************************************************/

    /**
     * 应急密码
     */
    @FormUrlEncoded
    @POST("index.php/api/room/native_password")
    Observable<DataInfo<NativePwdBean>> native_password(@Field("room_id") String room_id);

    /**
     * 录入指纹
     */
    @FormUrlEncoded
    @POST("index.php/api/device/set_fingerprint")
    Observable<DataInfo> addFingerprint(@Field("room_id") String room_id);

    /**
     * 录入指纹
     */
    @FormUrlEncoded
    @POST("index.php/api/device/set_ic_card")
    Observable<DataInfo> addIcCard(@Field("room_id") String room_id);

    /**
     * 指纹 ic卡 列表
     * <p>
     * type类型 1 遥控器 2 ic卡 3 指纹
     * page页码 默认1
     */
    @FormUrlEncoded
    @POST("index.php/api/device/finger_ic_list")
    Observable<DataInfo<PageDataBean<ClockSetManagerItemBean>>> getFingerIcList(@Field("room_id") String room_id,
                                                                                @Field("type") String type,
                                                                                @Field("page") String page);

    /**
     * ic卡 指纹 重命名
     */
    @FormUrlEncoded
    @POST("index.php/api/device/finger_ic_rename")
    Observable<DataInfo> renameFingerIc(@Field("id") String id,
                                        @Field("name") String name);

    /**
     * 删除 指纹 ic卡
     */
    @FormUrlEncoded
    @POST("index.php/api/device/finger_ic_remove")
    Observable<DataInfo> removeFingerIc(@Field("id") String id);

    /**
     * 查询 管理员密码 锁孔密码 临时密码
     * <p>
     * pwd_type 密码类型 0 管理员密码 1 临时密码 9 锁孔密码
     */
    @FormUrlEncoded
    @POST("index.php/api/device/lock_pwd")
    Observable<DataInfo<QueryPwdBean>> lockPwd(@Field("room_id") String room_id,
                                               @Field("pwd_type") String pwd_type);

    /**
     * 刷新 管理员密码 锁孔密码
     * <p>
     * pwd_type 密码类型 0 管理员密码 9 锁孔密码
     */
    @FormUrlEncoded
    @POST("index.php/api/device/password_refresh")
    Observable<DataInfo<QueryPwdBean>> passwordRefresh(@Field("room_id") String room_id,
                                                       @Field("pwd_type") String pwd_type);

    /**
     * 设置门锁密码
     * <p>
     * pwd_type 密码类型 1 临时密码 2普通密码    默认 2
     */
    @FormUrlEncoded
    @POST("index.php/api/device/set_password")
    Observable<DataInfo<QueryPwdBean>> setPassword(@Field("room_id") String room_id,
                                                   @Field("device_id") String device_id,
                                                   @Field("password") String password,
                                                   @Field("hours") String hours,
                                                   @Field("pwd_type") String pwd_type,
                                                   @Field("name") String name);

    /**
     * 删除密码
     */
    @FormUrlEncoded
    @POST("index.php/api/device/delete_password")
    Observable<DataInfo> deletePassword(@Field("device_id") String device_id,
                                        @Field("password_id") String password_id);

    /**
     * 密码列表
     */
    @FormUrlEncoded
    @POST("index.php/api/device/password_list")
    Observable<DataInfo<List<DevicesOpenPasswordBean>>> passwordList(@Field("device_id") String device_id);

    /**
     * 获取管理员密码 发短信验证码
     */
    @FormUrlEncoded
    @POST("index.php/api/index/getcode")
    Observable<DataInfo> getManageCode(@Field("room_id") String room_id,
                                       @Field("mobile") String mobile);

    /**
     * 获取管理员密码 确认
     */
    @FormUrlEncoded
    @POST("index.php/api/device/get_manage_pwd")
    Observable<DataInfo> getManagePwd(@Field("room_id") String room_id,
                                      @Field("mobile") String mobile,
                                      @Field("code") String code);

    /**
     * 获取承租人手机号
     */
    @FormUrlEncoded
    @POST("index.php/api/room/room_used_phone")
    Observable<DataInfo<RoomUserPhoneBean>> room_used_phone(@Field("room_id") String room_id);

    /***********************************************设备*************************************************************/

    /**
     * 获取设备类型列表
     */
    @FormUrlEncoded
    @POST("index.php/api/device/device_type")
    Observable<DataInfo<PageDataBean<MachineInfo>>> getDeviceType(@Field("page") String page);

    /**
     * 获取设备列表
     */
    @FormUrlEncoded
    @POST("index.php/api/device/index")
    Observable<DataInfo<PageDataBean<DeviceInfo>>> getDeviceList(@Field("room_id") String room_id,
                                                                 @Field("page") String page);

    /**
     * 同步服务费
     */
    @FormUrlEncoded
    @POST("index.php/api/device/sync_service_expire")
    Observable<DataInfo> sync_service_expire(@Field("room_id") String room_id,
                                             @Field("device_id") String device_id);


    /*********************************************2.3.4版本以后新接口*****************************************************/
    /**
     * 申请添加房产(2.3.4版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room/add_2_3_4")
    Observable<DataInfo<AddHouseBean>> addRoom2(
            @Field("room_name") String room_name,
            @Field("room_type") String room_type,
            @Field("room_config_id") String room_config_id,
            @Field("province") String province,
            @Field("city") String city,
            @Field("area") String area,
            @Field("map_address") String map_address,
            @Field("address") String address,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("measure_area") String measure_area,
            @Field("remark") String remark,
            @Field("image") String image,
            @Field("lng") String lng,
            @Field("lat") String lat);

    /**
     * 编辑房产（2.3.4版本以后）
     */
    @FormUrlEncoded
    @POST("index.php/api/room/edit_2_3_4")
    Observable<DataInfo> editRoom2(
            @Field("room_id") String room_id,
            @Field("room_name") String room_name,
            @Field("room_type") String room_type,
            @Field("room_config_id") String room_config_id,
            @Field("province") String province,
            @Field("city") String city,
            @Field("area") String area,
            @Field("map_address") String map_address,
            @Field("address") String address,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("measure_area") String measure_area,
            @Field("remark") String remark,
            @Field("image") String image,
            @Field("lng") String lng,
            @Field("lat") String lat);

    /**
     * 获取房东端房产列表(2.3.4版本以后)
     *
     * @page 页数 默认1
     */
    @GET("index.php/api/room/list_2_3_4")
    Observable<DataInfo<PageDataBean<HouseOrderInfoBean>>> getRoomList2(@Query("page") String page,
                                                                        @Query("keyword") String keyword);

    /**
     * 房东端房产详情(2.3.4版本以后)
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/info_2_3_4")
    Observable<DataInfo<HouseDetailsInfoBean2>> getHouseInfo2(@Field("room_id") String room_id);

    /**
     * 发布房产(2.3.4版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room/publish_2_3_4")
    Observable<DataInfo> publishHouse2(
            @Field("room_id") String room_id,//房产id
            @Field("type") String type,//1短租 2长租
            @Field("housing_price") String housing_price//租金
    );

    /**
     * 取消发布(2.3.4版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room/cancel_2_3_4")
    Observable<DataInfo> cancelPublishHouse2(@Field("room_id") String room_id);


    /**
     * 获取房产配置列表(2.3.4版本以后)
     */
    @GET("index.php/api/room/roomConfig_2_3_4")
    Observable<DataInfo<List<HouseConfigBean>>> getRoomConfigList2();

    /**
     * 推广码查询(2.3.4版本以后)
     *
     * @code 推广码
     */
    @FormUrlEncoded
    @POST("index.php/api/fee/promotionCodeDetail")
    Observable<DataInfo<PromotionCodeBean>> promotionCodeDetail(@Field("code") String code);

    /**
     * 安装费支付(2.3.4版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/pay/installOrderPay_2_3_4")
    Observable<DataInfo<PayBean>> installOrderPay2(@Field("payment") String payment,//1微信 2支付宝 3余额
                                                   @Field("room_id") String room_id,//房产id
                                                   @Field("code_id") String code_id,//推广码id
                                                   @Field("service_charge_id") String service_charge_id);//服务费id

    /*********************************************2.4.1版本以后新接口*****************************************************/
    /**
     * 添加订单(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/add_2_4_1")
    Observable<DataInfo<AddRentingBean>> addOrderPay2(@Field("room_id") String room_id,//房产id
                                                      @Field("phone") String phone,//手机号
                                                      @Field("start_time") String start_time,//开始日期 格式 2019-08-15 14:00:00
                                                      @Field("end_time") String end_time);//结束日期 格式 2019-08-16 14:00:00

    /**
     * 获取已租出日期(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/rentingDate_2_4_1")
    Observable<DataInfo<List<RentingDateBean>>> rentingDate2(@Field("room_id") String room_id);

    /**
     * 激活订单(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/activate")
    Observable<DataInfo<ActivateBean>> activate(@Field("account") String account);

    /**
     * 查询租客是否注册(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/user/check")
    Observable<DataInfo> check(@Field("phone") String phone);

    /**
     * 添加订单后调用详情(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/info_2_4_1")
    Observable<DataInfo<PwsOrderDetailsBean>> queryPassword2(@Field("order_id") String order_id);

    /**
     * 订单列表(2.4.1版本以后)
     */
    @GET("index.php/api/room_order/list_2_4_1")
    Observable<DataInfo<PageDataBean<RenterOrderListBean>>> getOrderList2(@Query("type") String type,
                                                                          @Query("page") String page);

    /**
     * 添加合租(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/add")
    Observable<DataInfo> addShareOrder(@Field("order_id") String order_id,
                                       @Field("phone") String phone);

    /**
     * 移除合租人(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_share_order/remove_2_4_1")
    Observable<DataInfo> removeShareOrder(@Field("order_id") String order_id,
                                          @Field("member_id") String member_id);

    /**
     * 租客列表(2.4.1版本以后)
     */
    @GET("index.php/api/room_share_order/list_2_4_1")
    Observable<DataInfo<List<ShareRentListBean>>> shareList(@Query("order_id") String order_id);

    /**
     * 房屋租客列表(2.4.1版本以后)
     */
    @GET("index.php/api/room/renterList")
    Observable<DataInfo<PageDataBean<RentListBean>>> renterList(@Query("page") String page,
                                                                @Query("room_id") String room_id);

    /**
     * 取消订单(2.4.1版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/cancel_2_4_1")
    Observable<DataInfo> canceOrder(@Field("order_id") String order_id);

    /**
     * 删除房产
     *
     * @room_id 房产id
     */
    @FormUrlEncoded
    @POST("index.php/api/room/delete")
    Observable<DataInfo> delHouse(@Field("room_id") String room_id);

    /*****************************************************版本2.4.2**********************************************/
    /**
     * 添加小区(2.4.2版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room/roomGroupAdd")
    Observable<DataInfo<RoomGroupListBean>> roomGroupAdd(@Field("name") String name,
                                                         @Field("province") String province,
                                                         @Field("city") String city,
                                                         @Field("area") String area,
                                                         @Field("business") String business,
                                                         @Field("address") String address,
                                                         @Field("lng") String lng,
                                                         @Field("lat") String lat);

    /**
     * 小区列表(2.4.2版本以后)
     */
    @GET("index.php/api/room/roomGroupList")
    Observable<DataInfo<PageDataBean<RoomGroupListBean>>> roomGroupList(@Query("page") String page,
                                                                        @Query("city") String city,
                                                                        @Query("keyword") String keyword);

    /**
     * 申请添加房产(2.4.2版本以后)
     */
    @FormUrlEncoded
    @POST("index.php/api/room/add_2_4_2")
    Observable<DataInfo<AddHouseBean>> addRoom3(
            @Field("room_name") String room_name,
            @Field("room_type") String room_type,
            @Field("room_config_id") String room_config_id,
            @Field("room_group_id") String room_group_id,
            @Field("address") String address,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("measure_area") String measure_area,
            @Field("remark") String remark,
            @Field("image") String image);

    /**
     * 编辑房产（2.3.4版本以后）
     */
    @FormUrlEncoded
    @POST("index.php/api/room/edit_2_4_2")
    Observable<DataInfo> editRoom3(
            @Field("room_id") String room_id,
            @Field("room_name") String room_name,
            @Field("room_type") String room_type,
            @Field("room_config_id") String room_config_id,
            @Field("room_group_id") String room_group_id,
            @Field("address") String address,
            @Field("total_floor") String total_floor,
            @Field("floor") String floor,
            @Field("measure_area") String measure_area,
            @Field("remark") String remark,
            @Field("image") String image);

    /**
     * 价格区间(2.4.2版本以后)
     */
    @GET("index.php/api/room/roomPriceArea")
    Observable<DataInfo<List<RoomPriceAreaBean>>> roomPriceArea(@Query("rent_type") String rent_type);

    /**
     * 房屋类型(2.4.2版本以后)
     */
    @GET("index.php/api/room/roomTypeList")
    Observable<DataInfo<List<RoomTypeListBean>>> roomTypeList();


    /**
     * 地图找房(2.4.2版本以后)
     */
    @GET("index.php/api/room/mapSearch")
    Observable<DataInfo<List<MapSearchBean>>> mapSearch(@Query("level") String level,
                                                        @Query("point1") String point1,
                                                        @Query("point2") String point2,
                                                        @Query("rent_type") String rent_type,
                                                        @Query("price_area_id") String price_area_id,
                                                        @Query("room_type_id") String room_type_id,
                                                        @Query("price_area") String price_area,
                                                        @Query("keyword") String keyword);

    /**
     * 小区房屋列表(2.4.2版本以后)
     */
    @GET("index.php/api/room/groupRoomList")
    Observable<DataInfo<PageDataBean<GroupRoomListBean>>> groupRoomList(@Query("page") String page,
                                                                        @Query("room_group_id") String room_group_id,
                                                                        @Query("rent_type") String rent_type,
                                                                        @Query("price_area_id") String price_area_id,
                                                                        @Query("room_type_id") String room_type_id,
                                                                        @Query("price_area") String price_area,
                                                                        @Query("keyword") String keyword);

    /**
     * 地图位置查询(2.4.2版本以后)
     */
    @GET("index.php/api/room/mapLocationSearch")
    Observable<DataInfo<List<MapLocationSearchBean>>> mapLocationSearch(@Query("city") String city,
                                                                        @Query("rent_type") String rent_type,
                                                                        @Query("keyword") String keyword);

    /**************************************************版本2.4.3*************************************************/
    /**
     * 添加银行卡
     */
    @FormUrlEncoded
    @POST("index.php/api/bank_card/add")
    Observable<DataInfo> addBankBean(@Field("card_no") String card_no,
                                     @Field("username") String username,
                                     @Field("phone") String phone);

    /**
     * 银行卡验证
     */
    @GET("index.php/api/bank_card/check")
    Observable<DataInfo<GetIssueBankBean>> getIssueBank(@Query("card_no") String card_no);

    /**
     * 银行卡列表
     */
    @GET("index.php/api/bank_card/index")
    Observable<DataInfo<PageDataBean<MyBankBean>>> getBankCardList(@Query("page") String page);

    /**
     * 删除银行卡
     */
    @FormUrlEncoded
    @POST("index.php/api/bank_card/del")
    Observable<DataInfo> delBankBean(@Field("card_id") String card_id);

    /**
     * 设置提现密码 适用设置，修改，重置
     */
    @FormUrlEncoded
    @POST("index.php/api/user/setWithdrawPassword")
    Observable<DataInfo> setWithdrawPassword(@Field("code") String code,
                                             @Field("withdraw_password") String withdraw_password);

    /**
     * 设置提现密码-密码验证
     * <p>
     * type  1 登入密码 2提现密码
     */
    @FormUrlEncoded
    @POST("index.php/api/user/checkPassword")
    Observable<DataInfo<CheckWithdrawPwdBean>> checkPassword(@Field("type") String type,
                                                             @Field("password") String password);

    /**
     * 设置提现密码-手机验证码验证
     */
    @FormUrlEncoded
    @POST("index.php/api/user/checkVerify")
    Observable<DataInfo<CheckWithdrawPwdBean>> checkVerify(@Field("verify") String verify);

    /**
     * 提现申请
     */
    @FormUrlEncoded
    @POST("index.php/api/user/withdraw")
    Observable<DataInfo> withdraw(@Field("code") String code,
                                  @Field("card_id") String card_id);

    /**
     * 租客下单
     */
    @FormUrlEncoded
    @POST("index.php/api/pay/rentOrder")
    Observable<DataInfo<PayBean>> rentOrder(@Field("room_id") String room_id,
                                            @Field("start_time") String start_time,
                                            @Field("end_time") String end_time,
                                            @Field("payment") String payment);//1微信 2支付宝

    /**
     * 继续支付
     */
    @FormUrlEncoded
    @POST("index.php/api/pay/continueRentOrder")
    Observable<DataInfo<PayBean>> continueRentOrder(@Field("order_id") String order_id,
                                                    @Field("payment") String payment);//1微信 2支付宝

    /**
     * 价格计算
     */
    @FormUrlEncoded
    @POST("index.php/api/room_order/price")
    Observable<DataInfo<RoomOederPriceBean>> rentOrderPrice(@Field("room_id") String room_id,
                                                            @Field("start_time") String start_time,
                                                            @Field("end_time") String end_time);

    /**
     * 开门记录
     */
    @FormUrlEncoded
    @POST("index.php/api/room/openRoomRecord")
    Observable<DataInfo<PageDataBean<DeviceHistoryBean>>> openRoomRecord(@Field("room_id") String room_id,
                                                                         @Field("page") String page);

    /**
     * 获取账单列表
     */
    @GET("index.php/api/landlord_center/accountBill_2_4_3")
    Observable<DataInfo<PageDataBean<BillListBean>>> getAccountBillList2(@Query("page") String page,
                                                                         @Query("type") String type,
                                                                         @Query("year") String year,
                                                                         @Query("month") String month);

    /**
     * 获取账单详情
     */
    @FormUrlEncoded
    @POST("index.php/api/landlord_center/billDetail_2_4_3")
    Observable<DataInfo<BillDetailBean>> getBillDetail2(@Field("id") String id);

    /**
     * 获取账单统计
     * <p>
     * month 月 0或者空 返回整年数据
     */
    @FormUrlEncoded
    @POST("index.php/api/landlord_center/billStatistics")
    Observable<DataInfo<BillStatisticsBean>> getBillStatistics(@Field("year") String year,
                                                               @Field("month") String month);

    /**
     * 计算密码-添加密码
     */
    @FormUrlEncoded
    @POST("index.php/api/generate_password/add")
    Observable<DataInfo<GeneratePwdBean>> addGeneratePassword(@Field("type") String type,//1 计次 2计时
                                                              @Field("password") String password,//管理员密码
                                                              @Field("device_id") String device_id,
                                                              @Field("num") String num,
                                                              @Field("start_time") String start_time,
                                                              @Field("end_time") String end_time);

    /**
     * 计算密码-删除密码
     */
    @FormUrlEncoded
    @POST("index.php/api/generate_password/del")
    Observable<DataInfo> delGeneratePassword(@Field("id") String id);

    /**
     * 获取密码列表
     */
    @GET("index.php/api/generate_password/index")
    Observable<DataInfo<List<GeneratePasswordBean>>> getGeneratePasswordList();
}
