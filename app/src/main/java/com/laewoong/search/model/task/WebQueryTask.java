package com.laewoong.search.model.task;

import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.response.QueryResponseWeb;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.model.ModelConstants;

import retrofit2.Call;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class WebQueryTask extends QueryTask<QueryResponseWeb, WebInfo> {

    public static final String TAG = WebQueryTask.class.getSimpleName();

    public WebQueryTask(NaverOpenAPIService service, String query, OnQueryTaskResponseListener<WebInfo> listener) {
        this(service, query, 1, listener);
    }

    public WebQueryTask(NaverOpenAPIService service, String query, int start, OnQueryTaskResponseListener<WebInfo> listener) {
        super(service, query, start, listener);
    }

    @Override
    public Call<QueryResponseWeb> getQuery() {

        return mService.queryWeb(mQuery, mStart, ModelConstants.DEFAULT_WEB_DISPALY);
    }
}
