package com.laewoong.search.di;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.laewoong.search.model.ImageResponseDataFactory;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.WebResponseDataFactory;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;

import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.disposables.CompositeDisposable;

@Component(modules = { RepositoryModule.class })
@Singleton
public interface RepositoryComponent {

    WebResponseDataFactory webResponseDataFactory();
    ImageResponseDataFactory imageResponseDataFactory();
    CompositeDisposable compositeDisposable();

    LiveData<PagedList<WebInfo>> webInfoList();
    LiveData<PagedList<ImageInfo>> imageInfoList();
}
