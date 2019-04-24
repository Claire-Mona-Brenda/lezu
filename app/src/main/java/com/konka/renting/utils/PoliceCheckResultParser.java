/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.konka.renting.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class PoliceCheckResultParser implements Parser<LivenessVsIdcardResult> {

    @Override
    public LivenessVsIdcardResult parse(String json) throws FaceException {

        Log.i("PoliceCheckResultParser", "LivenessVsIdcardResult->" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            /*if (jsonObject.has("error_code")) {
                FaceException error = new FaceException(jsonObject.optInt("error_code"),
                        jsonObject.optString("error_msg"));
                throw error;
            }

            LivenessVsIdcardResult result = new LivenessVsIdcardResult();
            result.setLogId(jsonObject.optLong("log_id"));
            result.setJsonRes(json);

            result.setScore(jsonObject.optDouble("result"));
            result.setIdcardImage(jsonObject.optString("matting_image"));

            JSONObject extInfoObject = jsonObject.optJSONObject("ext_info");
            if (extInfoObject != null) {
                double faceliveness =  extInfoObject.optDouble("faceliveness");
                result.setFaceliveness(faceliveness);
            }*/
            Gson mGson = new GsonBuilder().setLenient().create();
            LivenessVsIdcardResult result = mGson.fromJson(String.valueOf(jsonObject), LivenessVsIdcardResult.class);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            FaceException error = new FaceException(FaceException.ErrorCode.JSON_PARSE_ERROR,
                    "Json parse error:" + json, e);
            throw error;
        }
    }
}
