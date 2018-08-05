package com.laewoong.search.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.content.Context;
import android.support.annotation.NonNull;
import com.laewoong.search.model.response.ErrorResponse;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.QueryResponseImage;
import com.laewoong.search.util.NetworkState;
import com.laewoong.search.util.Util;
import java.io.IOException;
import java.lang.annotation.Annotation;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ImageResponseDataSource extends PageKeyedDataSource<Integer, ImageInfo> {

    private Context context;
    private NaverOpenAPIService mApiService;
    private String mQuery;

    private CompositeDisposable compositeDisposable;

    private MutableLiveData networkState;
    private MutableLiveData initialLoading;

    public ImageResponseDataSource(Context context, NaverOpenAPIService apiService, final String query, CompositeDisposable compositeDisposable) {
        this.context = context;
        mApiService = apiService;
        mQuery = query;

        this.compositeDisposable = compositeDisposable;

        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }

    public LiveData getNetworkState() {
        return networkState;
    }

    public LiveData getInitialLoading() {
        return initialLoading;
    }

    public void setQuery(final String query) {
        this.mQuery = query;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, ImageInfo> callback) {

        if(mQuery == null || mQuery.isEmpty()) {
            return;
        }

        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);


        compositeDisposable.add(mApiService.queryImage(mQuery, 1, params.requestedLoadSize,
                ModelConstants.DEFAULT_IMAGE_SORT,
                ModelConstants.DEFAULT_IMAGE_FILTER)
                .subscribe(response -> {

                    if(response.isSuccessful()) {
                        QueryResponseImage result = response.body();

                        int next = result.getStart() + result.getDisplay();
                        Integer nextPageKey = (next > result.getTotal()) ? null : Integer.valueOf(next);
                        callback.onResult(result.getItems(), result.getStart(), nextPageKey);

                        initialLoading.postValue(NetworkState.LOADED);
                        networkState.postValue(NetworkState.LOADED);
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

                        String errorMessage = Util.convertErrorCodeToMessage(context, mQuery, error.errorCode);

                        initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

                    }}
                    , throwable -> {

                            String errorMessage = (throwable == null) ? "unknown error" : throwable.getMessage();
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                })
        );
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ImageInfo> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ImageInfo> callback) {

        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(mApiService.queryImage(mQuery, params.key, params.requestedLoadSize,
                ModelConstants.DEFAULT_IMAGE_SORT,
                ModelConstants.DEFAULT_IMAGE_FILTER)
                .subscribe(response -> {

                    if(response.isSuccessful()) {
                        QueryResponseImage result = response.body();
                        int next = result.getStart() + result.getDisplay();
                        Integer nextPageKey = (next > result.getTotal()) ? null : Integer.valueOf(next);
                        callback.onResult(result.getItems(), nextPageKey);

                        networkState.postValue(NetworkState.LOADED);
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

                        String errorMessage = Util.convertErrorCodeToMessage(context, mQuery, error.errorCode);

                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                    }}
                    , throwable -> {
                            String errorMessage = (throwable == null) ? "unknown error" : throwable.getMessage();
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
                })
        );
    }
}
