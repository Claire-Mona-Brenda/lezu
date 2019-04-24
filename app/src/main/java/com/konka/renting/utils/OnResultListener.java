/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.konka.renting.utils;


public interface OnResultListener<T> {
    void onResult(T result);

    void onError(FaceException error);
}
