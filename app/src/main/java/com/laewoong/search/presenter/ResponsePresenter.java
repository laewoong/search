package com.laewoong.search.presenter;

import com.laewoong.search.model.OnQueryResponseListener;

/**
 * Created by laewoong on 2018. 4. 28..
 */

public interface ResponsePresenter {

    void show();
    void query(String query);
    void queryMore();
    OnQueryResponseListener getOnQueryResponseListener();
}
