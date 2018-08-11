package com.laewoong.search.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;

import com.laewoong.search.di.ContextModule;
import com.laewoong.search.di.DaggerRepositoryComponent;
import com.laewoong.search.di.RepositoryComponent;
import com.laewoong.search.di.RepositoryModule;
import com.laewoong.search.model.ImageResponseDataFactory;
import com.laewoong.search.model.WebResponseDataFactory;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.NetworkState;
import com.laewoong.search.view.ViewConstants;

import io.reactivex.disposables.CompositeDisposable;

public class SearchViewModel extends AndroidViewModel {

    //private MutableLiveData<String> query;
    private LiveData<PagedList<WebInfo>> webInfoList;
    private LiveData<PagedList<ImageInfo>> imageInfoList;
    //private MutableLiveData<Integer> selectedDetailImagePosition;
    private MutableLiveData<ViewConstants.TAB> curSelectedTab;
    private CompositeDisposable compositeDisposable;
    private LiveData<NetworkState> networkState;
    private WebResponseDataFactory webResponseDataFactory;
    private ImageResponseDataFactory imageResponseDataFactory;

    public SearchViewModel(Application application) {

        super(application);

        //query = new MutableLiveData<>();
        imageInfoList = new MutableLiveData<>();
        //selectedDetailImagePosition = new MutableLiveData<>();

        RepositoryComponent repositoryComponent = DaggerRepositoryComponent.builder()
                .contextModule(new ContextModule(getApplication()))
                .repositoryModule(new RepositoryModule())
                .build();
        webResponseDataFactory = repositoryComponent.webResponseDataFactory();
        imageResponseDataFactory = repositoryComponent.imageResponseDataFactory();
        compositeDisposable = repositoryComponent.compositeDisposable();

        webInfoList = repositoryComponent.webInfoList();
        imageInfoList = repositoryComponent.imageInfoList();

        //TODO: concat web with image.
        networkState = Transformations.switchMap(webResponseDataFactory.getWebResponseDataSourceLiveData(),
                dataSource -> dataSource.getNetworkState());

        curSelectedTab = new MutableLiveData<>();
        curSelectedTab.setValue(ViewConstants.TAB.WEB);
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

//    public MutableLiveData<String> getQuery() {
//        return query;
//    }
//
//    public void updatedQuery(final String newQuery) {
//        this.query.setValue(newQuery);
//    }

    public LiveData<PagedList<WebInfo>> getWebInfoList() {
        return webInfoList;
    }

    public LiveData<PagedList<ImageInfo>> getImageInfoList() {
        return imageInfoList;
    }

    public void queryWeb(final String query) {

        compositeDisposable.dispose();
        webResponseDataFactory.setQuery(query);
        webResponseDataFactory.getWebResponseDataSourceLiveData().getValue().invalidate();

    }

    public void queryImage(final String query) {

        compositeDisposable.dispose();
        imageResponseDataFactory.setQuery(query);
        imageResponseDataFactory.getImagebResponseDataSourceLiveData().getValue().invalidate();
    }

//    public LiveData<Integer> getSelectedDetailImagePosition() {
//        return selectedDetailImagePosition;
//    }
//
//    public void selectedDetailImagePosition(int position) {
//        selectedDetailImagePosition.setValue(position);
//    }

    public LiveData<ViewConstants.TAB> getCurSelectedTab() {
        return curSelectedTab;
    }

    public void selectedTabButton(ViewConstants.TAB tab) {
        this.curSelectedTab.setValue(tab);
    }
}
