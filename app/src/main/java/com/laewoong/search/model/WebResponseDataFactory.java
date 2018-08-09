package com.laewoong.search.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.CompositeDisposable;

@Singleton
public class WebResponseDataFactory extends DataSource.Factory {

    private Context context;
    private NaverOpenAPIService apiService;
    private CompositeDisposable compositeDisposable;
    private WebResponseDataSource webResponseDataSource;
    private MutableLiveData<WebResponseDataSource> webResponseDataSourceLiveData;
    private String query;

    @Inject
    public WebResponseDataFactory(Context context, NaverOpenAPIService apiService, CompositeDisposable compositeDisposable) {
        this.context = context;
        this.apiService = apiService;
        this.webResponseDataSourceLiveData = new MutableLiveData<WebResponseDataSource>();
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public DataSource create() {

        webResponseDataSource = new WebResponseDataSource(context, apiService, query, compositeDisposable);
        webResponseDataSourceLiveData.postValue(webResponseDataSource);
        return webResponseDataSource;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public LiveData<WebResponseDataSource> getWebResponseDataSourceLiveData() {
        return webResponseDataSourceLiveData;
    }
}
