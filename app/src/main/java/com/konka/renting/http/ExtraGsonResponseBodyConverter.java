package com.konka.renting.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;


/**
 * Created by jzxiang on 11/07/2018.
 */

public final class ExtraGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    ExtraGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            JSONObject response = new JSONObject(value.string());


            // 结果状态不对的，统一抛出异常，进入Subscriber的onError回调函数
            if (response.optInt("status") != 200) {
                value.close();
//                throw new ExtraApiException(response.optInt("status"), response.optString("message"));
            }

            // 后台返回不统一、不规范，客户端来背锅处理……
            String info = response.optString("json");
            if (TextUtils.isEmpty(info)) {
                info = response.optString("resultList");
            }
            if (TextUtils.isEmpty(info) || TextUtils.equals(info.toLowerCase(), "null")) {
                info = "{}";
            }

            MediaType contentType = value.contentType();
            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;


            InputStream inputStream = new ByteArrayInputStream(info.getBytes());
            Reader reader = new InputStreamReader(inputStream, charset);
            JsonReader jsonReader = gson.newJsonReader(reader);

            return adapter.read(jsonReader);
        } catch (JSONException e) {
            throw new IOException();
        } finally {
            value.close();
        }
    }
}