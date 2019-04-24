package com.konka.renting.landlord.house;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konka.renting.R;
import com.konka.renting.base.BaseFragment;
import com.konka.renting.landlord.house.view.HouseProxy;
import com.konka.renting.utils.UIUtils;

/**
 */
public class HouseFragment extends BaseFragment {


    public static HouseFragment newInstance() {

        Bundle args = new Bundle();

        HouseFragment fragment = new HouseFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    public HouseFragment() {
        // Required empty public constructor
    }


    public void onResume() {
        if(houseProxy!=null){
            houseProxy.houseRefresh(null);
        }
        super.onResume();
    }
    HouseProxy houseProxy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.lib_house_activity, container, false);
        ViewGroup viewGroup=  v.findViewById(R.id.layout_opendoor);
        viewGroup.setPadding(viewGroup.getPaddingLeft(),viewGroup.getPaddingTop()+UIUtils.getStatusHeight(),viewGroup.getPaddingRight(),viewGroup.getPaddingBottom());

        houseProxy=new HouseProxy();
        houseProxy.init(v);
        houseProxy.setActivity(getActivity());
        //http://let.tuokemao.com/index.php/Service/LandlordRenter/getLandlordRoomList
        return v;
    }

}
