package com.laewoong.search.di;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.laewoong.search.view.ViewConstants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {

    @ActivityScope
    @Provides
    @Named("web_list_layout_manger")
    public static RecyclerView.LayoutManager provideWebResponseLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @ActivityScope
    @Provides
    @Named("image_list_layout_manger")
    public static RecyclerView.LayoutManager provideImageResponseLayoutManager(Context context) {
       return new GridLayoutManager(context, ViewConstants.DEFAULT_GRID_SPAN_COUNT);
    }
}
