package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class HouseDetailsInfoBean implements Parcelable {

    /**
     * room_id : 422
     * room_no : 154340352131597538
     * room_name : 康佳啊啊啊
     * address : 广东省深圳市南山区康佳研发大厦B12
     * total_floor : 38
     * floor : 18
     * measure_area : 10
     * config :
     * explain :
     * image : ["https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/28/3c81756d13eddac7f4ba4cee5d17324f.jpg"]
     * auth_image : []
     * room_status : 1
     * housing_price : 0.00
     * type : 0
     * time : 2018-11-28 19:12:01
     * is_del : 1
     * service_status : 0
     * service_date : 0
     * thumb_auth_image : ["https://lezuxiaowo-test.youlejiakeji.com/uploads/image/2018/11/28/thumb_3c81756d13eddac7f4ba4cee5d17324f.jpg"]
     * agent : {"id":100000000,"agent_name":"深圳代理"}
     * province_name : {"id":6,"region_name":"广东省"}
     * city_name : {"id":77,"region_name":"深圳市"}
     * area_name : {"id":707,"region_name":"南山区"}
     * room_type : {"id":136,"name":"单间"}
     */

    private String room_id;
    private String room_no;
    private String room_name;
    private String address;
    private int total_floor;
    private int floor;
    private float measure_area;
    private List<HouseConfigBean> config;
    private String remark;
    private String explain;
    private int room_status;
    private String housing_price;
    private int type;
    private String time;
    private int is_del;
    private int service_status;
    private String service_date;
    private String device_id;
    private String gateway_id;
    private String gateway_version;
    private AgentBean agent;
    private ProvinceNameBean province_name;
    private CityNameBean city_name;
    private AreaNameBean area_name;
    private RoomTypeBean room_type;
    private List<String> image;
    private List<String> thumb_image;
    private List<String> auth_image;
    private List<String> thumb_auth_image;


    protected HouseDetailsInfoBean(Parcel in) {
        room_id = in.readString();
        room_no = in.readString();
        room_name = in.readString();
        address = in.readString();
        total_floor = in.readInt();
        floor = in.readInt();
        measure_area = in.readFloat();
        config = in.createTypedArrayList(HouseConfigBean.CREATOR);
        remark=in.readString();
        explain = in.readString();
        room_status = in.readInt();
        housing_price = in.readString();
        type = in.readInt();
        time = in.readString();
        is_del = in.readInt();
        service_status = in.readInt();
        service_date = in.readString();
        device_id = in.readString();
        gateway_id = in.readString();
        gateway_version = in.readString();
        agent = in.readParcelable(AgentBean.class.getClassLoader());
        province_name = in.readParcelable(ProvinceNameBean.class.getClassLoader());
        city_name = in.readParcelable(CityNameBean.class.getClassLoader());
        area_name = in.readParcelable(AreaNameBean.class.getClassLoader());
        room_type = in.readParcelable(RoomTypeBean.class.getClassLoader());
        image = in.createStringArrayList();
        thumb_image = in.createStringArrayList();
        auth_image = in.createStringArrayList();
        thumb_auth_image = in.createStringArrayList();
    }

    public static final Creator<HouseDetailsInfoBean> CREATOR = new Creator<HouseDetailsInfoBean>() {
        @Override
        public HouseDetailsInfoBean createFromParcel(Parcel in) {
            return new HouseDetailsInfoBean(in);
        }

        @Override
        public HouseDetailsInfoBean[] newArray(int size) {
            return new HouseDetailsInfoBean[size];
        }
    };

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotal_floor() {
        return total_floor;
    }

    public void setTotal_floor(int total_floor) {
        this.total_floor = total_floor;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public float getMeasure_area() {
        return measure_area;
    }

    public void setMeasure_area(float measure_area) {
        this.measure_area = measure_area;
    }

    public List<HouseConfigBean> getConfig() {
        return config;
    }

    public void setConfig(List<HouseConfigBean> config) {
        this.config = config;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getRoom_status() {
        return room_status;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public String getHousing_price() {
        return housing_price;
    }

    public void setHousing_price(String housing_price) {
        this.housing_price = housing_price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    public int getService_status() {
        return service_status;
    }

    public void setService_status(int service_status) {
        this.service_status = service_status;
    }

    public String getService_date() {
        return service_date;
    }

    public void setService_date(String service_date) {
        this.service_date = service_date;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getGateway_id() {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id) {
        this.gateway_id = gateway_id;
    }

    public String getGateway_version() {
        return gateway_version;
    }

    public void setGateway_version(String gateway_version) {
        this.gateway_version = gateway_version;
    }

    public AgentBean getAgent() {
        return agent;
    }

    public void setAgent(AgentBean agent) {
        this.agent = agent;
    }

    public ProvinceNameBean getProvince_name() {
        return province_name;
    }

    public void setProvince_name(ProvinceNameBean province_name) {
        this.province_name = province_name;
    }

    public CityNameBean getCity_name() {
        return city_name;
    }

    public void setCity_name(CityNameBean city_name) {
        this.city_name = city_name;
    }

    public AreaNameBean getArea_name() {
        return area_name;
    }

    public void setArea_name(AreaNameBean area_name) {
        this.area_name = area_name;
    }

    public RoomTypeBean getRoom_type() {
        return room_type;
    }

    public void setRoom_type(RoomTypeBean room_type) {
        this.room_type = room_type;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public List<String> getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(List<String> thumb_image) {
        this.thumb_image = thumb_image;
    }

    public List<String> getAuth_image() {
        return auth_image;
    }

    public void setAuth_image(List<String> auth_image) {
        this.auth_image = auth_image;
    }

    public List<String> getThumb_auth_image() {
        return thumb_auth_image;
    }

    public void setThumb_auth_image(List<String> thumb_auth_image) {
        this.thumb_auth_image = thumb_auth_image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(room_id);
        dest.writeString(room_no);
        dest.writeString(room_name);
        dest.writeString(address);
        dest.writeInt(total_floor);
        dest.writeInt(floor);
        dest.writeFloat(measure_area);
        dest.writeTypedList(config);
        dest.writeString(remark);
        dest.writeString(explain);
        dest.writeInt(room_status);
        dest.writeString(housing_price);
        dest.writeInt(type);
        dest.writeString(time);
        dest.writeInt(is_del);
        dest.writeInt(service_status);
        dest.writeString(service_date);
        dest.writeString(device_id);
        dest.writeString(gateway_id);
        dest.writeString(gateway_version);
        dest.writeParcelable(agent, flags);
        dest.writeParcelable(province_name, flags);
        dest.writeParcelable(city_name, flags);
        dest.writeParcelable(area_name, flags);
        dest.writeParcelable(room_type, flags);
        dest.writeStringList(image);
        dest.writeStringList(thumb_image);
        dest.writeStringList(auth_image);
        dest.writeStringList(thumb_auth_image);
    }

    public static class AgentBean implements Parcelable{
        /**
         * id : 100000000
         * agent_name : 深圳代理
         */

        private int id;
        private String agent_name;

        protected AgentBean(Parcel in) {
            id = in.readInt();
            agent_name = in.readString();
        }

        public static final Creator<AgentBean> CREATOR = new Creator<AgentBean>() {
            @Override
            public AgentBean createFromParcel(Parcel in) {
                return new AgentBean(in);
            }

            @Override
            public AgentBean[] newArray(int size) {
                return new AgentBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAgent_name() {
            return agent_name;
        }

        public void setAgent_name(String agent_name) {
            this.agent_name = agent_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(agent_name);
        }
    }

    public static class ProvinceNameBean implements Parcelable{
        /**
         * id : 6
         * region_name : 广东省
         */

        private int id;
        private String region_name;

        protected ProvinceNameBean(Parcel in) {
            id = in.readInt();
            region_name = in.readString();
        }

        public static final Creator<ProvinceNameBean> CREATOR = new Creator<ProvinceNameBean>() {
            @Override
            public ProvinceNameBean createFromParcel(Parcel in) {
                return new ProvinceNameBean(in);
            }

            @Override
            public ProvinceNameBean[] newArray(int size) {
                return new ProvinceNameBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(region_name);
        }
    }

    public static class CityNameBean implements Parcelable{
        /**
         * id : 77
         * region_name : 深圳市
         */

        private int id;
        private String region_name;

        protected CityNameBean(Parcel in) {
            id = in.readInt();
            region_name = in.readString();
        }

        public static final Creator<CityNameBean> CREATOR = new Creator<CityNameBean>() {
            @Override
            public CityNameBean createFromParcel(Parcel in) {
                return new CityNameBean(in);
            }

            @Override
            public CityNameBean[] newArray(int size) {
                return new CityNameBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(region_name);
        }
    }

    public static class AreaNameBean implements Parcelable{
        /**
         * id : 707
         * region_name : 南山区
         */

        private int id;
        private String region_name;

        protected AreaNameBean(Parcel in) {
            id = in.readInt();
            region_name = in.readString();
        }

        public static final Creator<AreaNameBean> CREATOR = new Creator<AreaNameBean>() {
            @Override
            public AreaNameBean createFromParcel(Parcel in) {
                return new AreaNameBean(in);
            }

            @Override
            public AreaNameBean[] newArray(int size) {
                return new AreaNameBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(region_name);
        }
    }

    public static class RoomTypeBean implements Parcelable{
        /**
         * id : 136
         * name : 单间
         */

        private int id;
        private String name;

        protected RoomTypeBean(Parcel in) {
            id = in.readInt();
            name = in.readString();
        }

        public static final Creator<RoomTypeBean> CREATOR = new Creator<RoomTypeBean>() {
            @Override
            public RoomTypeBean createFromParcel(Parcel in) {
                return new RoomTypeBean(in);
            }

            @Override
            public RoomTypeBean[] newArray(int size) {
                return new RoomTypeBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(name);
        }
    }
}
