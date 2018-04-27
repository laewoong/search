package com.laewoong.search;

import java.util.List;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public interface OnQueryResponseListener {

    void onSuccessResponse();
    void onEmptyResponse();
    void onFinalResponse();
}
