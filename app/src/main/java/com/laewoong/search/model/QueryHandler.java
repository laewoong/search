package com.laewoong.search.model;

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

    private List<OnQueryResponseListener> mWebQueryResponseClientListenerList;
    private List<OnQueryResponseListener> mImageQueryResponseClientListenerList;

    private final ExecutorService mExecutorService;

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

        mExecutorService = Executors.newCachedThreadPool(new LowPriorityThreadFactory());

        mService = NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);

        init();
    }

    private void init() {

        mWebQueryTaskResponseListener = new QueryTask.OnQueryTaskResponseListener<WebInfo>() {
            @Override
            public void onFailNetwork() {

                if(mWebQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResponseClientListenerList) {
                    listener.onFailNetwork();
                }
            }

            @Override
            public void onSuccessQueryResponse(List<WebInfo> infoList) {

                mWebInfoList.addAll(infoList);

                if(mWebQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResponseClientListenerList) {
                    listener.onSuccessResponse();
                }
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {

                if(mWebQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResponseClientListenerList) {

                    listener.onErrorQueryResponse(errorCode);
                }
            }

            @Override
            public void onEmptyQueryResponse() {
                if(mWebQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResponseClientListenerList) {

                    listener.onEmptyResponse();
                }
            }

            @Override
            public void onFinalQueryResponse() {

                if(mWebQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mWebQueryResponseClientListenerList) {
                    listener.onFinalResponse();
                }
            }
        };

        mImageQueryTaskResponseListener = new QueryTask.OnQueryTaskResponseListener<ImageInfo>() {
            @Override
            public void onFailNetwork() {

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResponseClientListenerList) {
                    listener.onFailNetwork();
                }
            }

            @Override
            public void onSuccessQueryResponse(List<ImageInfo> infoList) {

                mImageInfoList.addAll(infoList);

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResponseClientListenerList) {
                    listener.onSuccessResponse();
                }
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResponseClientListenerList) {

                    listener.onErrorQueryResponse(errorCode);
                }
            }

            @Override
            public void onEmptyQueryResponse() {

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResponseClientListenerList) {

                    listener.onEmptyResponse();
                }
            }

            @Override
            public void onFinalQueryResponse() {

                if(mImageQueryResponseClientListenerList.isEmpty()) {
                    return;
                }

                for(OnQueryResponseListener listener : mImageQueryResponseClientListenerList) {
                    listener.onFinalResponse();
                }
            }
        };
    }

    public void release() {

        mExecutorService.shutdownNow();

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

        // 다른 탭에서 이미지 탭으로 넘어온 경우 기존 탭 요청은 무효하므로 취소.
        if(mWebQueryTask != null) {
            mWebQueryTask.cancel();
        }

        if(mImageQueryTask != null) {
            mImageQueryTask.cancel();
        }

        mImageQueryTask = new ImageQueryTask(mService, query, mImageQueryTaskResponseListener);

        mExecutorService.execute(mImageQueryTask);
    }

    public void queryImageMore() {

        if(mImageQueryTask.isAlreadyArrivedFinalResponse() == false) {
            mExecutorService.execute(mImageQueryTask);
        }
    }
}
