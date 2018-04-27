package com.laewoong.search.model;

import android.util.Log;

import com.laewoong.search.OnQueryResponseListener;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.model.task.ImageQueryTask;
import com.laewoong.search.model.task.QueryTask;
import com.laewoong.search.model.task.WebQueryTask;
import com.laewoong.search.util.LowPriorityThreadFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class QueryHandler {

    private static final String TAG = QueryHandler.class.getSimpleName();

    private List<WebInfo>   mWebInfoList;
    private List<ImageInfo> mImageInfoList;

    private List<OnQueryResponseListener> mWebQueryResultListenerList;
    private List<OnQueryResponseListener> mImageQueryResultListenerList;

    private final ExecutorService mExecutorService;

    private NaverOpenAPIService mService;

    private WebQueryTask mWebQueryTask;
    private ImageQueryTask mImageQueryTask;

    private String mQuery;

    private QueryTask.OnQueryResponseListener<WebInfo> mWebQueryResponseListener;
    private QueryTask.OnQueryResponseListener<ImageInfo> mImageQueryResponseListener;

    public QueryHandler() {
        mWebInfoList    = new LinkedList<WebInfo>();
        mImageInfoList  = new LinkedList<ImageInfo>();

        mWebQueryResultListenerList = new LinkedList<OnQueryResponseListener>();
        mImageQueryResultListenerList = new LinkedList<OnQueryResponseListener>();

        mExecutorService = Executors.newCachedThreadPool(new LowPriorityThreadFactory());

        mService = NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);

        init();
    }

    private void init() {

        mWebQueryResponseListener = new QueryTask.OnQueryResponseListener<WebInfo>() {
            @Override
            public void onFailNetwork() {

                if(mWebQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResultListenerList) {
                    listener.onFailNetwork();
                }
            }

            @Override
            public void onSuccessQueryResponse(List<WebInfo> infoList) {

                mWebInfoList.addAll(infoList);

                if(mWebQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResultListenerList) {
                    listener.onSuccessResponse();
                }
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {

                if(mWebQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResultListenerList) {

                    listener.onErrorQueryResponse(errorCode);
                }
            }

            @Override
            public void onEmptyQueryResponse() {
                if(mWebQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResultListenerList) {

                    listener.onEmptyResponse();
                }
            }

            @Override
            public void onFinalQueryResponse() {

                if(mWebQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResultListenerList) {
                    listener.onFinalResponse();
                }
            }
        };

        mImageQueryResponseListener = new QueryTask.OnQueryResponseListener<ImageInfo>() {
            @Override
            public void onFailNetwork() {

                if(mImageQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResultListenerList) {
                    listener.onFailNetwork();
                }
            }

            @Override
            public void onSuccessQueryResponse(List<ImageInfo> infoList) {

                mImageInfoList.addAll(infoList);

                if(mImageQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResultListenerList) {
                    listener.onSuccessResponse();
                }
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {

                if(mImageQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResultListenerList) {

                    listener.onErrorQueryResponse(errorCode);
                }
            }

            @Override
            public void onEmptyQueryResponse() {

                if(mImageQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResultListenerList) {

                    listener.onEmptyResponse();
                }
            }

            @Override
            public void onFinalQueryResponse() {

                if(mImageQueryResultListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResultListenerList) {
                    listener.onFinalResponse();
                }
            }
        };
    }

    public void addWebQueryResultListener(OnQueryResponseListener listener) {
        mWebQueryResultListenerList.add(listener);
    }

    public boolean removeWebQueryResultListener(OnQueryResponseListener listener) {
        return mWebQueryResultListenerList.remove(listener);
    }

    public void addImageQueryResultListener(OnQueryResponseListener listener) {
        mImageQueryResultListenerList.add(listener);
    }

    public boolean removeImageQueryResultListener(OnQueryResponseListener listener) {
        return mImageQueryResultListenerList.remove(listener);
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

        mWebQueryTask = new WebQueryTask(mService, query, mWebQueryResponseListener);
        mExecutorService.execute(mWebQueryTask);
    }

    public void queryWebMore() {
        if(mWebQueryTask.isAlreadyArrivedFinalResponse() == false) {
            mExecutorService.execute(mWebQueryTask);
        }
    }

    public void queryImage(final String query) {

        mQuery = query.trim();

        mImageInfoList.clear();

        mImageQueryTask = new ImageQueryTask(mService, query, mImageQueryResponseListener);

        mExecutorService.execute(mImageQueryTask);
    }

    public void queryImageMore() {

        if(mImageQueryTask.isAlreadyArrivedFinalResponse() == false) {
            mExecutorService.execute(mImageQueryTask);
        }
    }
}
