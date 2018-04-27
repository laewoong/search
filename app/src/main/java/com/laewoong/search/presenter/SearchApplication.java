package com.laewoong.search.presenter;

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

    public QueryHandler getQueryHandler() {
        return mQueryHandler;
    }
}
