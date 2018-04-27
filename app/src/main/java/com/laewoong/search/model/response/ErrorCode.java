package com.laewoong.search.model.response;

/**
 * Created by laewoong on 2018. 4. 27..
 */

public enum ErrorCode {

    SE01("SE01"),
    SE02("SE02"),
    SE03("SE03"),
    SE04("SE04"),
    SE06("SE06"),
    SE05("SE05"),
    SE99("SE99"),

    NAVER_MAX_START_VALUE_POLICY("NAVER_MAX_START_VALUE_POLICY");

    private String mErrorCode;

    ErrorCode(String errorCode){
        mErrorCode = errorCode;
    }

    public String getErrorCode(){
        return mErrorCode;
    }
}
