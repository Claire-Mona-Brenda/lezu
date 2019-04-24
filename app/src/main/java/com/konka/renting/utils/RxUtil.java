package com.konka.renting.utils;


import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @date 创建时间：2017/3/25
 * @author 郭阳鹏
 * @description 处理线程与返回结果
 */
public class RxUtil {

    /**
     * 统一线程处理
     * @param <T>
     * @return IO--->UI线程
     */
    public static <T> Observable.Transformer<T, T> rxSchedulerHelper() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

//    /**
//     * 统一返回结果处理
//     * @param <T>
//     * @return
//     */
//    public static <T> Observable.Transformer<RetrofitHttpResponse<T>, T> handleMyResult() {   //compose判断结果
//        return new Observable.Transformer<RetrofitHttpResponse<T>, T>() {
//            @Override
//            public Observable<T> call(Observable<RetrofitHttpResponse<T>> httpResponseObservable) {
//                return httpResponseObservable.flatMap(new Func1<RetrofitHttpResponse<T>, Observable<T>>() {
//                    @Override
//                    public Observable<T> call(RetrofitHttpResponse<T> tHttpResponse) {
//                        if(tHttpResponse.getCode() == 200) {
//                            return createData(tHttpResponse.getData());
//                        } else {
//                            return Observable.error(new ApiException("服务器返回error"));
//                        }
//                    }
//                });
//            }
//        };
//    }

    /**
     * 生成Observable
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
