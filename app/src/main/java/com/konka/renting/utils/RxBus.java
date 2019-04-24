package com.konka.renting.utils;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @date 创建时间：2017/3/25
 * @author 郭阳鹏
 * @description 使用观察者模式实现各组件之间的通信
 */
public class RxBus {

    private final Subject<Object, Object> bus;
    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getDefault() {
        return RxBusHolder.sInstance;
    }

    private static class RxBusHolder {
        private static final RxBus sInstance = new RxBus();
    }


    // 提供了一个新的事件
    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    // 默认订阅
    public <T> Subscription toDefaultObservable(Class<T> eventType, Action1<T> act) {
        return bus.ofType(eventType).compose(RxUtil.<T>rxSchedulerHelper()).subscribe(act);
    }
}
