package com.laewoong.search;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class QueryHandler  {

    private static final String TAG = QueryHandler.class.getSimpleName();

    private int mStart = 1;

    private OnQueryResultListener mOnQueryResultListener;

    public QueryHandler() {

    }

    public void setOnQueryResultListener(OnQueryResultListener listener) {
        mOnQueryResultListener = listener;
    }


    public void queryWeb(final String keyword) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                NaverOpenAPIService gitHubService = NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);

                Call<SearchResultWeb> call = gitHubService.repoContributors(keyword, mStart, Constants.DEFAULT_DISPALY);

                try{
                    SearchResultWeb result = call.execute().body();

                    if(result == null) {
                        Log.i(TAG, "======== Result is null!!!!!!!");
                        return;
                    }

                    final List<WebInfo> infos = result.getItems();

                    if(infos.isEmpty()) {
                        Log.i(TAG, "======== infos is null!!!!!!!");
                        return;
                    }

                    //TODO : 검색 결과가 없을 경우
                    //TODO : 에러난 경우

                    mStart = result.getStart() + result.getDisplay();

                    Log.i(TAG, "==================== next : " + mStart);

                    mOnQueryResultListener.onSuccessQueryResult(infos);
                }
                catch (IOException e) {
                    Log.i(TAG, "IOException : " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
