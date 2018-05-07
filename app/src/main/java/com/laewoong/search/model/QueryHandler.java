package com.laewoong.search.model;

import android.util.Log;

import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.model.task.ImageQueryTask;
import com.laewoong.search.model.task.QueryTask;
import com.laewoong.search.model.task.WebQueryTask;

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

    private WebQueryTask mWebQueryTask;
    private ImageQueryTask mImageQueryTask;

    private String mQuery;

    private QueryTask.OnQueryTaskResponseListener<WebInfo> mWebQueryTaskResponseListener;
    private QueryTask.OnQueryTaskResponseListener<ImageInfo> mImageQueryTaskResponseListener;

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
        if(mImageQueryTask != null) {
            mImageQueryTask.cancel();
        }

        if(mWebQueryTask != null) {
            mWebQueryTask.cancel();
        }

        mWebQueryTask = new WebQueryTask(mService, query, mWebQueryTaskResponseListener);
        mWebQueryTask.run();
    }

    public void queryWebMore() {
        if(mWebQueryTask.isAlreadyArrivedFinalResponse() == false) {
            mWebQueryTask.run();
        }
    }

    public void queryImage(final String query) {

        mQuery = query.trim();

        mImageInfoList.clear();

        // 다른 탭에서 이미지 탭으로 넘어온 경우 기존 탭 요청은 무효하므로 취소.
        if(mWebQueryTask != null) {
            mWebQueryTask.cancel();
        }

        if(mImageQueryTask != null) {
            mImageQueryTask.cancel();
        }

        mImageQueryTask = new ImageQueryTask(mService, query, mImageQueryTaskResponseListener);

        mImageQueryTask.run();
    }

    public void queryImageMore() {

        if(mImageQueryTask.isAlreadyArrivedFinalResponse() == false) {
            mImageQueryTask.run();
        }
    }
}
