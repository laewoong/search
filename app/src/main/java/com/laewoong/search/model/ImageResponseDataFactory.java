package com.laewoong.search.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.content.Context;
import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;

public class ImageResponseDataFactory extends DataSource.Factory {

    private Context context;
    private NaverOpenAPIService apiService;
    private CompositeDisposable compositeDisposable;
    private ImageResponseDataSource imageResponseDataSource;
    private MutableLiveData<ImageResponseDataSource> imageResponseDataSourceLiveData;
    private String query;

    public ImageResponseDataFactory(Context context, NaverOpenAPIService apiService, CompositeDisposable compositeDisposable) {
        this.context = context;
        this.apiService = apiService;
        this.imageResponseDataSourceLiveData = new MutableLiveData<ImageResponseDataSource>();
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public DataSource create() {
        imageResponseDataSource = new ImageResponseDataSource(context, apiService, query, compositeDisposable);
        imageResponseDataSourceLiveData.postValue(imageResponseDataSource);
        return imageResponseDataSource;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public LiveData<ImageResponseDataSource> getImagebResponseDataSourceLiveData() {
        return imageResponseDataSourceLiveData;
    }
}
