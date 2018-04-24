package com.laewoong.search;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by laewoong on 2018. 4. 21..
 */

public class QueryHandler implements Serializable, WebQueryTask.OnWebQueryResponseListener, ImageQueryTask.OnImageQueryResponseListener {

    private static final String TAG = QueryHandler.class.getSimpleName();

    private List<WebInfo>   mWebInfoList;
    private List<ImageInfo> mImageInfoList;

    private List<OnQueryResponseListener> mWebQueryResultListenerList;
    private List<OnQueryResponseListener> mImageQueryResultListenerList;

    private final ExecutorService mExecutorService;

    private WebQueryTask mWebQueryTask;
    private ImageQueryTask mImageQueryTask;

    private String mQuery;

    public QueryHandler() {
        mWebInfoList    = new LinkedList<WebInfo>();
        mImageInfoList  = new LinkedList<ImageInfo>();

        mWebQueryResultListenerList = new LinkedList<OnQueryResponseListener>();
        mImageQueryResultListenerList = new LinkedList<OnQueryResponseListener>();

        mExecutorService = Executors.newCachedThreadPool(new LowPriorityThreadFactory());
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

        mQuery = query;

        mWebInfoList.clear();

        mWebQueryTask = new WebQueryTask(query, this);

        mExecutorService.execute(mWebQueryTask);
    }

    public void queryWebMore() {

        mExecutorService.execute(mWebQueryTask);
    }

    public void queryImage(final String query) {

        mQuery = query;

        mImageInfoList.clear();

        mImageQueryTask = new ImageQueryTask(query, this);

        mExecutorService.execute(mImageQueryTask);
    }

    public void queryImageMore() {

        mExecutorService.execute(mImageQueryTask);
    }

    @Override
    public void onSuccessWebQueryResponse(List<WebInfo> infoList) {

        mWebInfoList.addAll(infoList);

        if(mWebQueryResultListenerList.isEmpty()) {
            return;
        }

        for(OnQueryResponseListener listener : mWebQueryResultListenerList) {
            listener.onSuccessResponse();
        }
    }

    @Override
    public void onSuccessImageQueryResponse(List<ImageInfo> infoList) {

        mImageInfoList.addAll(infoList);

        if(mImageQueryResultListenerList.isEmpty()) {
            return;
        }

        for(OnQueryResponseListener listener : mImageQueryResultListenerList) {
            listener.onSuccessResponse();
        }
    }
}
