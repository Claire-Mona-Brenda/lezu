package com.konka.renting.landlord.house.widget;

import com.konka.renting.bean.CityBean;

import java.util.ArrayList;
import java.util.List;

import cn.addapp.pickers.picker.LinkagePicker;

public class CityDataProvider implements LinkagePicker.DataProvider {
    private List<String> firstList = new ArrayList();
    private List<String> secondList = new ArrayList();
    private List<String> thirdList = new ArrayList();
    private boolean onlyTwo = true;

    public CityDataProvider(List<CityBean> firstList, List<CityBean> secondList, List<CityBean> thirdList) {
        for (CityBean bean:firstList) {
            this.firstList.add(bean.getName());
        }
        for (CityBean bean:secondList) {
            this.secondList.add(bean.getName());
        }
        for (CityBean bean:thirdList) {
            this.thirdList.add(bean.getName());
        }

    }

    public boolean isOnlyTwo() {
        return this.onlyTwo;
    }

    public List<String> provideFirstData() {
        return this.firstList;
    }

    public List<String> provideSecondData(int firstIndex) {
        return this.secondList;
    }

    public List<String> provideThirdData(int firstIndex, int secondIndex) {
        return this.onlyTwo ? new ArrayList() : this.thirdList;
    }
}
