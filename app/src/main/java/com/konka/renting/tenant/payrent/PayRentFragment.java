package com.konka.renting.tenant.payrent;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.konka.renting.R;
import com.konka.renting.bean.PayRentRefreshEvent;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.UIUtils;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayRentFragment extends Fragment {

    public static PayRentFragment newInstance() {
        
        Bundle args = new Bundle();
        
        PayRentFragment fragment = new PayRentFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private CompositeSubscription mCompositeSubscription;
    PayRentPresent payRentPresent;
    public PayRentFragment() {
        addRxBusSubscribe(PayRentRefreshEvent.class, new Action1<PayRentRefreshEvent>() {
            @Override
            public void call(PayRentRefreshEvent locationRefreshEvent) {

                payRentPresent.getData();
                Log.d("jia","刷新租客订单");
            }
        });
        // Required empty public constructor
    }

    protected <U> void addRxBusSubscribe(Class<U> eventType, Action1<U> act) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(eventType, act));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.lib_payrent_activity, container, false);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.text_title).getParent();
        ViewGroup.LayoutParams lp = viewGroup.getLayoutParams();
        lp.height += UIUtils.getStatusHeight();
        viewGroup.setLayoutParams(lp);
        viewGroup.setPadding(viewGroup.getPaddingLeft(), viewGroup.getPaddingTop() + UIUtils.getStatusHeight(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
         payRentPresent=new PayRentPresent();
        payRentPresent.initView(view);
        return view;
    }

}
