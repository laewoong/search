package com.laewoong.search.controller;

import com.laewoong.search.model.OnQueryResponseListener;

/**
 * Created by laewoong on 2018. 4. 28..
 */

public interface QueryResponseController {

    void show();
    void query(String query);
    void queryMore();
    OnQueryResponseListener getOnQueryResponseListener();
}
