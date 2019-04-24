package com.konka.renting.bean;

import java.util.List;

public class PageDataBean<T> {

    /**
     * list : [{"room_id":418,"room_no":"15427865732160","room_name":"rrr","address":"浙江省杭州市滨江区rrr","image":"https://lettest.youlejiakeji.com/Uploads/Image/App/20181121/15427865736859.jpg","room_status":0,"is_del":1,"type":0,"service_date":0},{"room_id":417,"room_no":"15426986425127","room_name":"大好河山","address":"浙江省杭州市滨江区瘦瘦高高","image":"https://lettest.youlejiakeji.com/Uploads/Image/App/20181120/15426986410854.jpg","room_status":0,"is_del":1,"type":0,"service_date":0},{"room_id":413,"room_no":"15417589106389","room_name":"西丽地铁站","address":"广东省深圳市南山区西丽地铁站","image":"https://lettest.youlejiakeji.com/Uploads/Image/App/20181109/15417589094571.jpg","room_status":5,"is_del":1,"type":2,"service_date":"2018-11-23"},{"room_id":412,"room_no":"15417582525192","room_name":"西乡客运站","address":"广东省深圳市宝安区西乡客运站","image":"https://lettest.youlejiakeji.com/Uploads/Image/App/20181109/15417582516857.jpg","room_status":5,"is_del":1,"type":2,"service_date":0},{"room_id":411,"room_no":"15414024440149","room_name":"裕安居C602","address":"广东省深圳市宝安区新安街道裕安居C座602","image":"https://lettest.youlejiakeji.com/Uploads/Image/App/20181105/15414024449812.jpg","room_status":0,"is_del":1,"type":0,"service_date":0}]
     * count : 5
     * page : 1
     * limit : 10
     */

    private int count;
    private int page;
    private int limit;
    private int totalPage;
    private List<T> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
