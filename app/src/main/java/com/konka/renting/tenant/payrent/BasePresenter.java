package com.konka.renting.tenant.payrent;

import com.konka.renting.utils.RxBus;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jbl on 18-3-28.
 */

public class BasePresenter {
    private CompositeSubscription mCompositeSubscription;

    public void addSubscrebe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }
    protected <U> void addRxBusSubscribe(Class<U> eventType, Action1<U> act) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(RxBus.getDefault().toDefaultObservable(eventType, act));
    }

}
