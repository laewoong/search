package com.laewoong.search;

import com.laewoong.search.model.response.ErrorCode;

import java.util.List;

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
