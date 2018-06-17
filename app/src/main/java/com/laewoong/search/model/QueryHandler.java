package com.laewoong.search.model;

import android.arch.lifecycle.LifecycleOwner;
import android.util.Log;

import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.QueryResponseImage;
import com.laewoong.search.model.response.QueryResponseWeb;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.model.task.QueryTask;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class QueryHandler {

    private static final String TAG = QueryHandler.class.getSimpleName();

    private List<WebInfo>   mWebInfoList;
    private List<ImageInfo> mImageInfoList;

    private List<OnQueryResponseListener> mWebQueryResponseClientListenerList;
    private List<OnQueryResponseListener> mImageQueryResponseClientListenerList;

    private NaverOpenAPIService mService;

    private QueryTask mQueryTask;

    private String mQuery;

    private QueryTask.OnQueryTaskResponseListener<WebInfo> mWebQueryTaskResponseListener;
    private QueryTask.OnQueryTaskResponseListener<ImageInfo> mImageQueryTaskResponseListener;

    private LifecycleOwner lifecycleOwner;

    public QueryHandler(LifecycleOwner owner) {
        this.lifecycleOwner = owner;
    }

    public QueryHandler() {
        mWebInfoList    = new LinkedList<WebInfo>();
        mImageInfoList  = new LinkedList<ImageInfo>();

        mWebQueryResponseClientListenerList = new LinkedList<OnQueryResponseListener>();
        mImageQueryResponseClientListenerList = new LinkedList<OnQueryResponseListener>();

        mService = NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);

        init();
    }

    private void init() {

        mWebQueryTaskResponseListener = new QueryTask.OnQueryTaskResponseListener<WebInfo>() {
            @Override
            public void onFailNetwork() {

                Observable.fromIterable(mWebQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onFailNetwork());
            }

            @Override
            public void onSuccessQueryResponse(List<WebInfo> infoList) {

                mWebInfoList.addAll(infoList);

                Observable.fromIterable(mWebQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onSuccessResponse());
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {

                Observable.fromIterable(mWebQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onErrorQueryResponse(errorCode));
            }

            @Override
            public void onEmptyQueryResponse() {

                Observable.fromIterable(mWebQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onEmptyResponse());
            }

            @Override
            public void onFinalQueryResponse() {

                Observable.fromIterable(mWebQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onFinalResponse());
            }
        };

        mImageQueryTaskResponseListener = new QueryTask.OnQueryTaskResponseListener<ImageInfo>() {
            @Override
            public void onFailNetwork() {

                Observable.fromIterable(mImageQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onFailNetwork());
            }

            @Override
            public void onSuccessQueryResponse(List<ImageInfo> infoList) {

                mImageInfoList.addAll(infoList);

                Observable.fromIterable(mImageQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onSuccessResponse());
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                Observable.fromIterable(mImageQueryResponseClientListenerList)
                        .subscribe(listener -> onErrorQueryResponse(errorCode));
            }

            @Override
            public void onEmptyQueryResponse() {

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                Observable.fromIterable(mImageQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onEmptyResponse());
            }

            @Override
            public void onFinalQueryResponse() {

                Observable.fromIterable(mImageQueryResponseClientListenerList)
                        .subscribe(listener -> listener.onFinalResponse());
            }
        };
    }

    public void release() {

        mWebInfoList.clear();
        Log.i("fff", "================================= here1111");
        mImageInfoList.clear();

        mWebQueryResponseClientListenerList.clear();
        mImageQueryResponseClientListenerList.clear();
    }

    public void addWebQueryResultListener(OnQueryResponseListener listener) {
        mWebQueryResponseClientListenerList.add(listener);
    }

    public boolean removeWebQueryResultListener(OnQueryResponseListener listener) {
        return mWebQueryResponseClientListenerList.remove(listener);
    }

    public void addImageQueryResultListener(OnQueryResponseListener listener) {
        mImageQueryResponseClientListenerList.add(listener);
    }

    public boolean removeImageQueryResultListener(OnQueryResponseListener listener) {
        return mImageQueryResponseClientListenerList.remove(listener);
    }

    public List<WebInfo> getWebInfoList() {
        return mWebInfoList;
    }

    public List<ImageInfo> getImageInfoList() {
        return mImageInfoList;
    }

    public String getQuery() {
        return mQuery;
    }

    public void queryWeb(final String query) {

        mQuery = query.trim();

        mWebInfoList.clear();

        // 다른 탭에서 웹 탭으로 넘어온 경우 기존 탭 요청은 무효하므로 취소.
        if(mQueryTask != null) {
            mQueryTask.cancel();
        }

        mQueryTask = new QueryTask<QueryResponseWeb, WebInfo>(query, 1, mWebQueryTaskResponseListener,
                (_query, _start) -> mService.queryWeb(_query, _start, ModelConstants.DEFAULT_WEB_DISPALY));

        mQueryTask.run();
    }

    public void queryImage(final String query) {

        mQuery = query.trim();
        Log.i("fff", "================================= 222222");
        mImageInfoList.clear();

        // 다른 탭에서 이미지 탭으로 넘어온 경우 기존 탭 요청은 무효하므로 취소.
        if(mQueryTask != null) {
            mQueryTask.cancel();
        }

        mQueryTask = new QueryTask<QueryResponseImage, ImageInfo>(query, 1, mImageQueryTaskResponseListener,
                (_query, _start) -> mService.queryImage(_query, _start,
                        ModelConstants.DEFAULT_IMAGE_DISPALY,
                        ModelConstants.DEFAULT_IMAGE_SORT,
                        ModelConstants.DEFAULT_IMAGE_FILTER));

        mQueryTask.run();
    }

    public void queryMore() throws IllegalStateException {

        if(mQueryTask == null) {
            throw new IllegalStateException("이전 요청이 존재하지 않습니다.");
        }

        if(mQueryTask.isAlreadyArrivedFinalResponse() == false) {
            mQueryTask.run();
        }
    }
}
