package com.laewoong.search.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.laewoong.search.R;
import com.laewoong.search.model.OnQueryResponseListener;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.view.WebResponseFragment;

import java.util.LinkedList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private QueryHandler queryHandler;

    private MutableLiveData<String> curFragmentTag;

    private MutableLiveData<String> query;
    private MutableLiveData<List<WebInfo>> webInfoList;
    private MutableLiveData<List<ImageInfo>> imageInfoList;

    private MutableLiveData<String> errorMessage;

    private MutableLiveData<Integer> selectedDetailImagePosition;

    public SearchViewModel(Application application) {

        super(application);

        queryHandler = new QueryHandler();

        curFragmentTag = new MutableLiveData<>();
        curFragmentTag.setValue(WebResponseFragment.TAG);

        query = new MutableLiveData<>();
        webInfoList = new MutableLiveData<>();
        imageInfoList = new MutableLiveData<>();
Log.i("fff", "====[WARONG] NEW SearViewModel Create!");
        errorMessage = new MutableLiveData<>();
        selectedDetailImagePosition = new MutableLiveData<>();

        init();
    }

    private void init() {
        queryHandler.addWebQueryResultListener(new OnQueryResponseListener() {
            @Override
            public void onFailNetwork() {
                errorMessage.setValue(getApplication().getString(R.string.guide_check_network_state));
            }

            @Override
            public void onSuccessResponse() {
                webInfoList.setValue(queryHandler.getWebInfoList());
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {
                final String message = (errorCode == ErrorCode.NAVER_MAX_START_VALUE_POLICY) ? getApplication().getString(R.string.guide_naver_max_start_value_policy) : getApplication().getString(R.string.guide_internal_error);
                errorMessage.setValue(message);
            }

            @Override
            public void onEmptyResponse() {
                //mWebResponseFragment.handleEmptyQueryResult();
            }

            @Override
            public void onFinalResponse() {
                // TODO handleFinalQueryResult()
                errorMessage.setValue(getApplication().getString(R.string.guide_final_query_response));
            }
        });

        queryHandler.addImageQueryResultListener(new OnQueryResponseListener() {
            @Override
            public void onFailNetwork() {
                errorMessage.setValue(getApplication().getString(R.string.guide_check_network_state));
            }

            @Override
            public void onSuccessResponse() {
                imageInfoList.setValue(queryHandler.getImageInfoList());
            }

            @Override
            public void onErrorQueryResponse(ErrorCode errorCode) {
                final String message = (errorCode == ErrorCode.NAVER_MAX_START_VALUE_POLICY) ? getApplication().getString(R.string.guide_naver_max_start_value_policy) : getApplication().getString(R.string.guide_internal_error);
                errorMessage.setValue(message);
            }

            @Override
            public void onEmptyResponse() {

            }

            @Override
            public void onFinalResponse() {
                // TODO handleFinalQueryResult()
                errorMessage.setValue(getApplication().getString(R.string.guide_final_query_response));
            }
        });
    }

    public MutableLiveData<String> getCurFragmentTag() {
        return curFragmentTag;
    }

    public MutableLiveData<String> getQuery() {
        return query;
    }

    public MutableLiveData<List<WebInfo>> getWebInfoList() {
        return webInfoList;
    }

    public MutableLiveData<List<ImageInfo>> getImageInfoList() {
        return imageInfoList;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void queryWeb(String query) {
        queryHandler.queryWeb(query);
    }

    public void queryImage(String query) {
        queryHandler.queryImage(query);
    }

    public MutableLiveData<Integer> getSelectedDetailImagePosition() {
        return selectedDetailImagePosition;
    }
}
