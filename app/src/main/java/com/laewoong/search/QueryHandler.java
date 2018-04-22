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

    private OnQueryResponseListener mOnQueryResultListener;

    public QueryHandler() {

    }

    public void setOnQueryResultListener(OnQueryResponseListener listener) {
        mOnQueryResultListener = listener;
    }


    public void queryWeb(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NaverOpenAPIWebService service = NaverOpenAPIWebService.retrofit.create(NaverOpenAPIWebService.class);

                Call<QueryResponseWeb> call = service.repoContributors(query, mStart, Constants.DEFAULT_DISPALY);

                try{
                    QueryResponseWeb result = call.execute().body();

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

                    mOnQueryResultListener.onResponseWeb(infos);
                }
                catch (IOException e) {
                    Log.i(TAG, "IOException : " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void queryImage(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                NaverOpenAPIImageService service = NaverOpenAPIImageService.retrofit.create(NaverOpenAPIImageService.class);

                Call<QueryResponseImage> call = service.repoContributors(query, mStart, Constants.DEFAULT_IMAGE_DISPALY, Constants.DEFAULT_IMAGE_SORT, Constants.DEFAULT_IMAGE_FILTER);

                try{
                    QueryResponseImage result = call.execute().body();

                    if(result == null) {
                        Log.i(TAG, "======== Result is null!!!!!!!");
                        return;
                    }

                    final List<ImageInfo> infos = result.getItems();

                    if(infos.isEmpty()) {
                        Log.i(TAG, "======== infos is null!!!!!!!");
                        return;
                    }

                    //TODO : 검색 결과가 없을 경우
                    //TODO : 에러난 경우

                    mStart = result.getStart() + result.getDisplay();

                    Log.i(TAG, "==================== next : " + mStart);

                    mOnQueryResultListener.onResponseImage(infos);
                }
                catch (IOException e) {
                    Log.i(TAG, "IOException : " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
