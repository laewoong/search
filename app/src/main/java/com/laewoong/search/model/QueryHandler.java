package com.laewoong.search.model;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.paging.PagedList;
import android.content.Context;
import android.util.Log;

import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ErrorResponse;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.QueryResponseImage;
import com.laewoong.search.model.response.QueryResponseWeb;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.model.task.QueryTask;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class QueryHandler {

    private static final String TAG = QueryHandler.class.getSimpleName();

    private NaverOpenAPIService mService;
    private String mQuery;
    private int mStart;
    private int mTotal;

    public QueryHandler() {
        mService = NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);
    }

    public void release() {
        mService = null;
    }

    public String getQuery() {
        return mQuery;
    }

    private Single<List<WebInfo>> queryAsWeb(String query, int start) {

        return mService.queryWeb(query, start, ModelConstants.DEFAULT_WEB_DISPALY)
                .map(response -> {

                    if(response.isSuccessful()) {
                        QueryResponseWeb result = response.body();

                        if(result.getItems().isEmpty()) {
                            mTotal = 0;
                            throw new ErrorResponse("ARRIVED_EMPTY_RESPONSE","");
                        }

                        mStart = result.getStart() + result.getDisplay();
                        mTotal = result.getTotal();
                        return result.getItems();
                    }
                    else{
                        Converter<ResponseBody, ErrorResponse> errorConverter =
                                NaverOpenAPIService.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

                        ErrorResponse error;


                        try{
                            error = errorConverter.convert(response.errorBody());

                        }catch (IOException e) {

                            error = new ErrorResponse("FAIL_NETWORK", "IOException : " + e.getMessage());
                        }

                        throw error;
                    }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<WebInfo>> queryWeb(final String query) {

        mQuery = query.trim();
        mStart = 1;

        return queryAsWeb(mQuery, mStart);
    }

    public Single<List<WebInfo>> queryWebMore() {
        if(mQuery == null) {
            return Single.error(new ErrorResponse());
        }

        if(mStart > mTotal) {
            // 더이상 데이터 없다는 정보 보내기
            return Single.error(new ErrorResponse("ARRIVED_FINAL_RESPONSE", ""));
        }

        return queryAsWeb(mQuery, mStart);
    }

    private Single<List<ImageInfo>> queryAsImage(String query, int start) {
        return mService.queryImage(query, start,
                ModelConstants.DEFAULT_IMAGE_DISPALY,
                ModelConstants.DEFAULT_IMAGE_SORT,
                ModelConstants.DEFAULT_IMAGE_FILTER)
                .map(response -> {
                    if(response.isSuccessful()) {
                        QueryResponseImage result = response.body();

                        if(result.getItems().isEmpty()) {
                            mTotal = 0;
                            throw new ErrorResponse("ARRIVED_EMPTY_RESPONSE","");
                        }

                        mStart = result.getStart() + result.getDisplay();
                        mTotal = result.getTotal();
                        return result.getItems();
                    }
                    else{
                        Converter<ResponseBody, ErrorResponse> errorConverter =
                                NaverOpenAPIService.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);

                        ErrorResponse error;

                        try{
                            error = errorConverter.convert(response.errorBody());
                        }catch (IOException e) {

                            error = new ErrorResponse("FAIL_NETWORK", "IOException : " + e.getMessage());
                        }

                        throw error;
                    }})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<List<ImageInfo>> queryImage(final String query) {

        mQuery = query.trim();
        mStart = 1;

        return queryAsImage(mQuery, mStart);
    }

    public Single<List<ImageInfo>> queryImageMore() {
        if(mQuery == null) {
            return Single.error(new ErrorResponse());
        }

        if(mStart > mTotal) {
            // 더이상 데이터 없다는 정보 보내기
            return Single.error(new ErrorResponse("ARRIVED_FINAL_RESPONSE", ""));
        }

        return queryAsImage(mQuery, mStart);
    }
}
