package com.laewoong.search.controller;

import android.app.Application;

import com.laewoong.search.model.QueryHandler;

/**
 * Created by laewoong on 2018. 4. 25..
 */

public class SearchApplication extends Application {

    private QueryHandler mQueryHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mQueryHandler = new QueryHandler();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mQueryHandler.release();
    }

    public QueryHandler getQueryHandler() {
        return mQueryHandler;
    }
}
