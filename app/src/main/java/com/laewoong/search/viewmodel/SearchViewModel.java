package com.laewoong.search.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.laewoong.search.controller.SearchApplication;
import com.laewoong.search.model.ImageResponseDataFactory;
import com.laewoong.search.model.ModelConstants;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.WebResponseDataFactory;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ErrorResponse;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.NetworkState;
import com.laewoong.search.view.ViewConstants;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.disposables.CompositeDisposable;

public class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<String> query;
    private LiveData<PagedList<WebInfo>> webInfoList;
    private LiveData<PagedList<ImageInfo>> imageInfoList;
    private MutableLiveData<Integer> selectedDetailImagePosition;
    private MutableLiveData<ViewConstants.TAB> curSelectedTab;
    private NaverOpenAPIService apiService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Executor executor;
    private LiveData<NetworkState> networkState;
    private WebResponseDataFactory webResponseDataFactory;
    private ImageResponseDataFactory imageResponseDataFactory;

    public SearchViewModel(Application application) {

        super(application);

        query = new MutableLiveData<>();
        imageInfoList = new MutableLiveData<>();
        selectedDetailImagePosition = new MutableLiveData<>();

        executor = Executors.newFixedThreadPool(5);
        apiService = ((SearchApplication)getApplication()).getApiService();
        initWebResponsePagedList();
        initImageResponsePagedList();

        curSelectedTab = new MutableLiveData<>();
        curSelectedTab.setValue(ViewConstants.TAB.WEB);
    }

    private void initWebResponsePagedList() {

        webResponseDataFactory = new WebResponseDataFactory(getApplication(), apiService, compositeDisposable);
        networkState = Transformations.switchMap(webResponseDataFactory.getWebResponseDataSourceLiveData(),
                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(ModelConstants.DEFAULT_WEB_DISPALY)
                        .setPageSize(ModelConstants.DEFAULT_WEB_DISPALY)
                        .build();

        webInfoList = (new LivePagedListBuilder(webResponseDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }

    private void initImageResponsePagedList() {

        imageResponseDataFactory = new ImageResponseDataFactory(getApplication(), apiService, compositeDisposable);

        //TODO: concat web with image.
//        networkState = Transformations.switchMap(imageResponseDataFactory.getImagebResponseDataSourceLiveData(),
//                dataSource -> dataSource.getNetworkState());

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(ModelConstants.DEFAULT_IMAGE_DISPALY)
                        .setPageSize(ModelConstants.DEFAULT_IMAGE_DISPALY)
                        .build();

        imageInfoList = (new LivePagedListBuilder(imageResponseDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public MutableLiveData<String> getQuery() {
        return query;
    }

    public void updatedQuery(final String newQuery) {
        this.query.setValue(newQuery);
    }

    public LiveData<PagedList<WebInfo>> getWebInfoList() {
        return webInfoList;
    }

    public LiveData<PagedList<ImageInfo>> getImageInfoList() {
        return imageInfoList;
    }

    public void queryWeb(String query) {

        webResponseDataFactory.setQuery(query);
        webResponseDataFactory.getWebResponseDataSourceLiveData().getValue().invalidate();

    }

    public void queryImage(String query) {
        imageResponseDataFactory.setQuery(query);
        imageResponseDataFactory.getImagebResponseDataSourceLiveData().getValue().invalidate();
    }

    public LiveData<Integer> getSelectedDetailImagePosition() {
        return selectedDetailImagePosition;
    }

    public void selectedDetailImagePosition(int position) {
        selectedDetailImagePosition.setValue(position);
    }

    public LiveData<ViewConstants.TAB> getCurSelectedTab() {
        return curSelectedTab;
    }

    public void selectedTabButton(ViewConstants.TAB tab) {
        this.curSelectedTab.setValue(tab);
    }
}
