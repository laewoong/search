package com.laewoong.search.di;

import android.content.Context;

import com.laewoong.search.controller.SearchApplication;
import com.laewoong.search.view.MainActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, MainActivityModule.class})
public interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Context context);
        ApplicationComponent build();
    }

    void inject(SearchApplication app);
}
