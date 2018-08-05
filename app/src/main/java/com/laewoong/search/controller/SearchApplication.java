package com.laewoong.search.controller;

import android.app.Application;

import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.QueryHandler;

/**
 * Created by laewoong on 2018. 4. 25..
 */

public class SearchApplication extends Application {

    private QueryHandler mQueryHandler;
    private NaverOpenAPIService apiService;

    @Override
    public void onCreate() {
        super.onCreate();

        mQueryHandler = new QueryHandler();
        apiService = NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mQueryHandler.release();
    }

    public QueryHandler getQueryHandler() {
        return mQueryHandler;
    }

    public NaverOpenAPIService getApiService() {
        return this.apiService;
    }
}
