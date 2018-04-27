package com.laewoong.search.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by laewoong on 2018. 4. 27..
 */

public class ErrorResponse {

    @SerializedName("errorMessage")
    public String errorMessage;

    @SerializedName("errorCode")
    public String errorCode;

    @Override
    public String toString() {
        return "errorMessage : " + errorMessage + ", errorCode : " + errorCode + "\n";
    }
}
