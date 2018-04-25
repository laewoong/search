package com.laewoong.search;

import android.app.Application;

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
