package com.laewoong.search.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by laewoong on 2018. 4. 27..
 */

public class ErrorResponse extends Exception {

    @SerializedName("errorMessage")
    public String errorMessage;

    @SerializedName("errorCode")
    public String errorCode;

    public ErrorResponse() {}

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "errorMessage : " + errorMessage + ", errorCode : " + errorCode + "\n";
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}
