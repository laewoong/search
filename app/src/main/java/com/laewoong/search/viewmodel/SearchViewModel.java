package com.laewoong.search.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ErrorResponse;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.view.WebResponseFragment;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private QueryHandler queryHandler;

    private MutableLiveData<String> curFragmentTag;

    private MutableLiveData<String> query;
    private MutableLiveData<List<WebInfo>> webInfoList;
    private MutableLiveData<List<ImageInfo>> imageInfoList;

    private MutableLiveData<Integer> selectedDetailImagePosition;
    private MutableLiveData<Boolean> isReachedList;

    private MutableLiveData<ErrorCode> errorCode;

    public SearchViewModel(Application application) {

        super(application);

        queryHandler = new QueryHandler();

        curFragmentTag = new MutableLiveData<>();
        curFragmentTag.setValue(WebResponseFragment.TAG);

        query = new MutableLiveData<>();
        webInfoList = new MutableLiveData<>();
        imageInfoList = new MutableLiveData<>();
        selectedDetailImagePosition = new MutableLiveData<>();

        isReachedList = new MutableLiveData<>();

        errorCode = new MutableLiveData<>();
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

    public void queryWeb(String query) {
        queryHandler.queryWeb(query)
                    .subscribe(response -> {
                        webInfoList.setValue(response);
                    },
                    error -> {
                        if(error instanceof ErrorResponse) {
                            ErrorResponse response = (ErrorResponse)error;
                            errorCode.setValue(ErrorCode.valueOf(response.errorCode));
                        }
                    });
    }

    public void queryWebMore() {
        queryHandler.queryWebMore()
                    .subscribe(response -> {
                                webInfoList.getValue().addAll(response);
                                webInfoList.setValue(webInfoList.getValue());
                            },
                            error -> {
                                if(error instanceof ErrorResponse) {
                                    ErrorResponse response = (ErrorResponse)error;
                                    errorCode.setValue(ErrorCode.valueOf(response.errorCode));
                                }
                            });
    }

    public void queryImage(String query) {
        queryHandler.queryImage(query)
                .subscribe(response -> {
                            imageInfoList.setValue(response);
                        },
                        error -> {
                            if(error instanceof ErrorResponse) {
                                ErrorResponse response = (ErrorResponse)error;
                                errorCode.setValue(ErrorCode.valueOf(response.errorCode));
                            }
                        });
    }

    public void queryImageMore() {
        queryHandler.queryImageMore()
                .subscribe(response -> {
                            imageInfoList.getValue().addAll(response);
                            imageInfoList.setValue(imageInfoList.getValue());
                        },
                        error -> {
                            if(error instanceof ErrorResponse) {
                                if(error instanceof ErrorResponse) {
                                    ErrorResponse response = (ErrorResponse)error;
                                    errorCode.setValue(ErrorCode.valueOf(response.errorCode));
                                }
                            }
                        });
    }

    public MutableLiveData<Integer> getSelectedDetailImagePosition() {
        return selectedDetailImagePosition;
    }

    public void reachedEndOfList() {
        isReachedList.setValue(true);
    }

    public LiveData<Boolean> getStatusOfReachingEndOfList() {
        return isReachedList;
    }

    public LiveData<ErrorCode> getErrorCode() {
        return errorCode;
    }
}
