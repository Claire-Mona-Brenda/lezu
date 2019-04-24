package com.konka.renting.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class AppConfigBean implements Parcelable {
    /**
     * version : {"id":7,"version":"1.3.0","is_forced":0,"url":"http://www.ddd","describe":"33433","time":"2019-01-11 18:07:55"}
     * disclaimer : {"image":"image_15481435976e3b5e9d1f98cc8843eb5ddeeefff33a.jpg","content":"梵蒂冈梵蒂冈电饭锅电饭锅地方电饭锅电饭锅地方佛挡杀佛"}
     * service_tel : 0755-86724540
     * user_protocol : 平台说明
     * common_problem : 常见问题
     */

    private VersionBean version;
    private DisclaimerBean disclaimer;
    private String service_tel;
    private String user_protocol;
    private String common_problem;

    protected AppConfigBean(Parcel in) {
        service_tel = in.readString();
        user_protocol = in.readString();
        common_problem = in.readString();
    }

    public static final Creator<AppConfigBean> CREATOR = new Creator<AppConfigBean>() {
        @Override
        public AppConfigBean createFromParcel(Parcel in) {
            return new AppConfigBean(in);
        }

        @Override
        public AppConfigBean[] newArray(int size) {
            return new AppConfigBean[size];
        }
    };

    public VersionBean getVersion() {
        return version;
    }

    public void setVersion(VersionBean version) {
        this.version = version;
    }

    public DisclaimerBean getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(DisclaimerBean disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getService_tel() {
        return service_tel;
    }

    public void setService_tel(String service_tel) {
        this.service_tel = service_tel;
    }

    public String getUser_protocol() {
        return user_protocol;
    }

    public void setUser_protocol(String user_protocol) {
        this.user_protocol = user_protocol;
    }

    public String getCommon_problem() {
        return common_problem;
    }

    public void setCommon_problem(String common_problem) {
        this.common_problem = common_problem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_tel);
        dest.writeString(user_protocol);
        dest.writeString(common_problem);
    }

    public static class VersionBean implements Parcelable {
        /**
         * id : 7
         * version : 1.3.0
         * is_forced : 0
         * url : http://www.ddd
         * describe : 33433
         * time : 2019-01-11 18:07:55
         */

        private int id;
        private String version;
        private int is_forced;
        private String url;
        private String describe;
        private String time;

        protected VersionBean(Parcel in) {
            id = in.readInt();
            version = in.readString();
            is_forced = in.readInt();
            url = in.readString();
            describe = in.readString();
            time = in.readString();
        }

        public static final Creator<VersionBean> CREATOR = new Creator<VersionBean>() {
            @Override
            public VersionBean createFromParcel(Parcel in) {
                return new VersionBean(in);
            }

            @Override
            public VersionBean[] newArray(int size) {
                return new VersionBean[size];
            }
        };

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getIs_forced() {
            return is_forced;
        }

        public void setIs_forced(int is_forced) {
            this.is_forced = is_forced;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(version);
            dest.writeInt(is_forced);
            dest.writeString(url);
            dest.writeString(describe);
            dest.writeString(time);
        }
    }

    public static class DisclaimerBean implements Parcelable {
        /**
         * image : image_15481435976e3b5e9d1f98cc8843eb5ddeeefff33a.jpg
         * content : 梵蒂冈梵蒂冈电饭锅电饭锅地方电饭锅电饭锅地方佛挡杀佛
         */

        private String image;
        private String content;

        protected DisclaimerBean(Parcel in) {
            image = in.readString();
            content = in.readString();
        }

        public static final Creator<DisclaimerBean> CREATOR = new Creator<DisclaimerBean>() {
            @Override
            public DisclaimerBean createFromParcel(Parcel in) {
                return new DisclaimerBean(in);
            }

            @Override
            public DisclaimerBean[] newArray(int size) {
                return new DisclaimerBean[size];
            }
        };

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(image);
            dest.writeString(content);
        }
    }
}
