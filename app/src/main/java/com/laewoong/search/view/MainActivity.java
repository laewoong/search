package com.laewoong.search.view;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import com.laewoong.search.R;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.viewmodel.SearchViewModel;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private SearchView  mSearchView;
    private BackPressCloseHandler mBackPressCloseHandler;
    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (SearchView)findViewById(R.id.searchview_query);
        mBackPressCloseHandler = new BackPressCloseHandler(this);
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        init();
    }

    private void setCurFragment(final String tag) {

        Fragment fragment = null;
        if(tag.equals(WebResponseFragment.TAG)) {
            fragment = getSupportFragmentManager().findFragmentByTag(WebResponseFragment.TAG);
            if(fragment == null) {
                fragment = new WebResponseFragment();
            }
        }
        else if(tag.equals(ImageResponseFragment.TAG)) {
            fragment = getSupportFragmentManager().findFragmentByTag(ImageResponseFragment.TAG);
            if(fragment == null) {
                fragment = new ImageResponseFragment();
            }
        }

        if(fragment == null) {
            return;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, fragment, tag).commit();
    }


    private void init() {

        // Init SearchView
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = query.trim();

                if(query.isEmpty()) {
                    return false;
                }

                mSearchView.clearFocus();
                searchViewModel.getQuery().setValue(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryRefinementEnabled(true);

        Observable<String> webButtonObservable = RxView.clicks(findViewById(R.id.button_web))
                .map(event -> WebResponseFragment.TAG);

        Observable<String> imageButtonObservale = RxView.clicks(findViewById(R.id.button_image))
                .map(event -> ImageResponseFragment.TAG);

        Observable.merge(webButtonObservable, imageButtonObservale)
                .subscribe(TAG -> {
                    searchViewModel.getCurFragmentTag().setValue(TAG);
                });

        // Init tab view's hint color
        SegmentedGroup tabGroup = (SegmentedGroup)findViewById(R.id.container_tab);
        tabGroup.setTintColor(Color.parseColor("#F06292"));

        searchViewModel.getCurFragmentTag().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String fragmentTag) {

                setCurFragment(fragmentTag);

                mSearchView.clearFocus();
            }
        });

        searchViewModel.getErrorCode().observe(this, new Observer<ErrorCode>() {
                @Override
                public void onChanged(@Nullable ErrorCode errorCode) {

                    String message;

                    switch (errorCode) {
                        case NAVER_MAX_START_VALUE_POLICY:
                            message = getApplication().getString(R.string.guide_naver_max_start_value_policy);
                            break;
                        case FAIL_NETWORK:
                            message = getApplication().getString(R.string.guide_check_network_state);
                            break;
                        case ARRIVED_FINAL_RESPONSE:
                            message = getApplication().getString(R.string.guide_final_query_response);
                            break;
                        case ARRIVED_EMPTY_RESPONSE:
                            message = String.format(getString(R.string.guide_empty_query_response), searchViewModel.getQuery().getValue());
                            break;
                        default:
                            message = getApplication().getString(R.string.guide_internal_error);
                    }

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            mBackPressCloseHandler.onBackPressed();
        }
    }
}