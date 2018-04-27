package com.laewoong.search.model.task;

import android.util.Log;

import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ErrorResponse;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.response.QueryResponse;

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

    public interface OnQueryResponseListener<E> {

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
    protected OnQueryResponseListener<E> mOnWebQueryResponseListener;

    public QueryTask(NaverOpenAPIService service, String query, int start, OnQueryResponseListener<E> listener) {

        mService = service;
        mQuery = query;
        mStart = start;
        mOnWebQueryResponseListener = listener;
    }

    @Override
    public void run() {

        getQuery().enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, Response<T> response) {

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

                        mStart = result.getStart() + result.getDisplay();
                    }

                    if(mStart > result.getTotal()) {
                        if(mOnWebQueryResponseListener != null) {

                            mOnWebQueryResponseListener.onFinalQueryResponse();
                        }
                    }
                }
                else {
                    Converter<ResponseBody, ErrorResponse> errorConverter =
                            NaverOpenAPIService.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    try{

                        ErrorResponse error = errorConverter.convert(response.errorBody());
                        mOnWebQueryResponseListener.onErrorQueryResponse(ErrorCode.valueOf(error.errorCode));

                    }catch (IOException e) {
                        Log.i(TAG, "IOException : " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {

                mOnWebQueryResponseListener.onFailNetwork();
            }
        });

    }

    public abstract Call<T> getQuery();
}