package com.laewoong.search.model.task;

import android.util.Log;

import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.response.QueryResponseWeb;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.Constants;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class WebQueryTask extends QueryTask<QueryResponseWeb, WebInfo> {

    public static final String TAG = WebQueryTask.class.getSimpleName();

    public WebQueryTask(NaverOpenAPIService service, String query, OnQueryResponseListener<WebInfo> listener) {
        this(service, query, 1, listener);
    }

    public WebQueryTask(NaverOpenAPIService service, String query, int start, OnQueryResponseListener<WebInfo> listener) {
        super(service, query, start, listener);
    }

    @Override
    public Call<QueryResponseWeb> getQuery() {

        return mService.queryWeb(mQuery, mStart, Constants.DEFAULT_WEB_DISPALY);
    }
}
