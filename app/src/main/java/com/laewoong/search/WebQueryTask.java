package com.laewoong.search;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class WebQueryTask implements Runnable {

    public interface OnWebQueryResponseListener {

        void onSuccessWebQueryResponse(List<WebInfo> infoList);
    }

    public static final String TAG = WebQueryTask.class.getSimpleName();

    private String mQuery;
    private int mStart;
    private OnWebQueryResponseListener mOnWebQueryResponseListener;

    public WebQueryTask(String query, OnWebQueryResponseListener listener) {
        this(query, 1, listener);
    }

    public WebQueryTask(String query, int start, OnWebQueryResponseListener listener) {
        mQuery = query;
        mStart = start;
        mOnWebQueryResponseListener = listener;
    }

    @Override
    public void run() {

        NaverOpenAPIWebService service = NaverOpenAPIWebService.retrofit.create(NaverOpenAPIWebService.class);

        Call<QueryResponseWeb> call = service.repoContributors(mQuery, mStart, Constants.DEFAULT_WEB_DISPALY);

        try{
            QueryResponseWeb result = call.execute().body();

            if(result == null) {
                Log.i(TAG, "======== Result is null!!!!!!!");
                return;
            }

            List<WebInfo> webInfoList = result.getItems();

            if(webInfoList.isEmpty()) {
                Log.i(TAG, "======== infos is null!!!!!!!");
                return;
            }

            //TODO : 검색 결과가 없을 경우
            //TODO : 에러난 경우

            mStart = result.getStart() + result.getDisplay();

            Log.i(TAG, "==================== next : " + mStart);

            if(mOnWebQueryResponseListener != null) {

                mOnWebQueryResponseListener.onSuccessWebQueryResponse(webInfoList);
            }
        }
        catch (IOException e) {
            Log.i(TAG, "IOException : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
