/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.konka.renting.utils;


/**
 * JSON解析
 * @param <T>
 */
public interface Parser<T> {
    T parse(String json) throws FaceException, FaceException;
}
