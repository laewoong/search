package com.laewoong.search.model;

import android.util.Log;

import com.laewoong.search.util.Constants;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

/**
 * Created by laewoong on 2018. 4. 23..
 */

public class ImageQueryTask implements Runnable {

    public interface OnImageQueryResponseListener {

        void onSuccessImageQueryResponse(List<ImageInfo> infoList);
        void onEmptyImageQueryResponse();
        void onFinalImageQueryResponse();
    }

    public static final String TAG = ImageQueryTask.class.getSimpleName();

    private String mQuery;
    private int mStart;
    private OnImageQueryResponseListener mOnImageQueryResponseListener;
    private boolean mIsReachTheEnd;

    public ImageQueryTask(String query, OnImageQueryResponseListener listener) {
        this(query, 1, listener);
    }

    public ImageQueryTask(String query, int start, OnImageQueryResponseListener listener) {
        mQuery = query;
        mStart = start;
        mOnImageQueryResponseListener = listener;
        mIsReachTheEnd = false;
    }

    public boolean isReachTheEnd() {
        return mIsReachTheEnd;
    }

    @Override
    public void run() {

        NaverOpenAPIImageService service = NaverOpenAPIImageService.retrofit.create(NaverOpenAPIImageService.class);

        Call<QueryResponseImage> call = service.repoContributors(mQuery, mStart, Constants.DEFAULT_IMAGE_DISPALY, Constants.DEFAULT_IMAGE_SORT, Constants.DEFAULT_IMAGE_FILTER);

        try{
            QueryResponseImage result = call.execute().body();

            if(result == null) {
                Log.i(TAG, "======== Result is null!!!!!!!");
                return;
            }

            List<ImageInfo> imageInfoList = result.getItems();

            if(imageInfoList.isEmpty()) {

                if(mOnImageQueryResponseListener != null) {

                    mOnImageQueryResponseListener.onEmptyImageQueryResponse();
                }
                return;
            }

            //TODO : 에러난 경우

            mStart = result.getStart() + result.getDisplay();

            if((mStart-1) <= result.getTotal()) {

                if(mOnImageQueryResponseListener != null) {

                    mOnImageQueryResponseListener.onSuccessImageQueryResponse(imageInfoList);
                }

                mStart = result.getStart() + result.getDisplay();
            }

            if(mStart > result.getTotal()) {
                if(mOnImageQueryResponseListener != null) {

                    mOnImageQueryResponseListener.onFinalImageQueryResponse();
                }
            }

            Log.i(TAG, "==================== next : " + mStart);


        }
        catch (IOException e) {
            Log.i(TAG, "IOException : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
