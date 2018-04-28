package com.laewoong.search.model.task;

import android.util.Log;

import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ErrorResponse;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.response.QueryResponse;
import com.laewoong.search.model.ModelConstants;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by laewoong on 2018. 4. 27..
 */

public abstract class QueryTask<T extends QueryResponse, E> implements Runnable {

        public interface OnQueryTaskResponseListener<E> {

        void onFailNetwork();
        void onSuccessQueryResponse(List<E> infoList);
        void onErrorQueryResponse(ErrorCode errorCode);
        void onEmptyQueryResponse();
        void onFinalQueryResponse();
    }

    public static final String TAG = WebQueryTask.class.getSimpleName();

    protected NaverOpenAPIService mService;
    protected String mQuery;
    protected int mStart;
    protected OnQueryTaskResponseListener<E> mOnWebQueryResponseListener;
    protected boolean mIsAlreadyArrivedFinalResponse; // 마지막 데이터가 로딩된 경우, 더이상 데이터 요청 하지 않기 위해 존재.
    protected Call<T> mCall;

    public QueryTask(NaverOpenAPIService service, String query, int start, OnQueryTaskResponseListener<E> listener) {

        mService = service;
        mQuery = query;
        mStart = start;
        mOnWebQueryResponseListener = listener;
        mIsAlreadyArrivedFinalResponse = false;
    }

    @Override
    public void run() {

        if(mStart > ModelConstants.MAX_START_VALUE) {

            if(mOnWebQueryResponseListener != null) {

                mOnWebQueryResponseListener.onErrorQueryResponse(ErrorCode.NAVER_MAX_START_VALUE_POLICY);
            }

            return;
        }

        mCall = getQuery();
        mCall.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, Response<T> response) {

                if(Thread.currentThread().isInterrupted()) {

                    return;
                }

                if (response.isSuccessful()) {

                    T result = response.body();

                    List<E> webInfoList = result.getItems();

                    if(webInfoList.isEmpty()) {

                        if(mOnWebQueryResponseListener != null) {

                            mOnWebQueryResponseListener.onEmptyQueryResponse();
                        }
                        return;
                    }

                    mStart = result.getStart() + result.getDisplay();

                    if((mStart-1) <= result.getTotal()) {

                        if(mOnWebQueryResponseListener != null) {

                            mOnWebQueryResponseListener.onSuccessQueryResponse(webInfoList);
                        }

                        // 같은 검색어로 추가 쿼리 요청을 위해 다음 start 값으로 변경.
                        mStart = result.getStart() + result.getDisplay();
                    }

                    if(mStart > result.getTotal()) {
                        if(mOnWebQueryResponseListener != null) {

                            mIsAlreadyArrivedFinalResponse = true;
                            mOnWebQueryResponseListener.onFinalQueryResponse();
                        }
                    }
                }
                else {
                    Converter<ResponseBody, ErrorResponse> errorConverter =
                            NaverOpenAPIService.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    try{

                        ErrorResponse error = errorConverter.convert(response.errorBody());

                        if(mOnWebQueryResponseListener != null) {

                            mOnWebQueryResponseListener.onErrorQueryResponse(ErrorCode.valueOf(error.errorCode));
                        }

                    }catch (IOException e) {
                        Log.i(TAG, "IOException : " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {

                if((Thread.currentThread().isInterrupted()) || mCall.isCanceled()) {
                    return;
                }

                mOnWebQueryResponseListener.onFailNetwork();
            }
        });

    }

    public boolean isAlreadyArrivedFinalResponse() {
        return mIsAlreadyArrivedFinalResponse;
    }

    public void cancel() {

        if((mCall != null) & (mCall.isCanceled() == false)) {
           mCall.cancel();
        }
    }

    public abstract Call<T> getQuery();
}
