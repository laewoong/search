package com.laewoong.search.model;

import com.laewoong.search.model.response.ErrorCode;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public interface OnQueryResponseListener {

    void onFailNetwork();
    void onSuccessResponse();
    void onErrorQueryResponse(ErrorCode errorCode);
    void onEmptyResponse();
    void onFinalResponse();
}
