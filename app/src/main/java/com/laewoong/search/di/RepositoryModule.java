package com.laewoong.search.di;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import com.laewoong.search.model.ImageResponseDataFactory;
import com.laewoong.search.model.ModelConstants;
import com.laewoong.search.model.NaverOpenAPIService;
import com.laewoong.search.model.WebResponseDataFactory;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.response.WebInfo;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.inject.Named;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class RepositoryModule {

    private Context context;

    public RepositoryModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public static NaverOpenAPIService provideApiService() {
        return NaverOpenAPIService.retrofit.create(NaverOpenAPIService.class);
    }

    @Provides
    @Singleton
    public CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    public Context provideContext() {
        return this.context;
    }

    @Provides
    @Singleton
    public Executor provideExecutor() {
        return Executors.newFixedThreadPool(5);
    }

    @Provides
    @Singleton
    public LiveData<PagedList<WebInfo>> provideWebInfoList(WebResponseDataFactory webResponseDataFactory, @Named("config_web") PagedList.Config pagedListConfig, Executor executor) {
        return (new LivePagedListBuilder(webResponseDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }

    @Provides
    @Singleton
    @Named("config_web")
    public PagedList.Config provideWebPagedListConfig() {
        return (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(ModelConstants.DEFAULT_WEB_DISPALY)
                        .setPageSize(ModelConstants.DEFAULT_WEB_DISPALY)
                        .build();
    }

    @Provides
    @Singleton
    public LiveData<PagedList<ImageInfo>> provideImageInfoList(ImageResponseDataFactory imageResponseDataFactory, @Named("config_image") PagedList.Config pagedListConfig, Executor executor) {
        return (new LivePagedListBuilder(imageResponseDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();
    }

    @Provides
    @Singleton
    @Named("config_image")
    public PagedList.Config provideImagePagedListConfig() {
        return (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(ModelConstants.DEFAULT_IMAGE_DISPALY)
                .setPageSize(ModelConstants.DEFAULT_IMAGE_DISPALY)
                .build();
    }

}
