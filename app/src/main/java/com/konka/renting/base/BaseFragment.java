package com.konka.renting.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.konka.renting.R;
import com.konka.renting.utils.RxBus;
import com.konka.renting.utils.UIUtils;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jzxiang on 4/3/17.
 */

public class BaseFragment extends Fragment {

    public static String TAG = "";
    public View mRootView;
    protected Activity mActivity;
    protected CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
    }

    public void init() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unSubscribe();
    }

    /**
     * 添加订阅
     *
     * @param subscription
     */
    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    /**
     * 管理各组件之间的通信
     *
     * @param eventType
     * @param act
     * @param <U>
     */
    protected <U> void addRxBusSubscribe(Class<U> eventType, Action1<U> act) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(eventType, act));
    }

    /**
     * 取消订阅
     */
    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    public void dismiss() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public void doFailed(){
        UIUtils.showFailed();
    }

    public void doSuccess(){
        UIUtils.showSuccess();
    }

    public void showToast(String string){
        UIUtils.displayToast(string);
    }

}
