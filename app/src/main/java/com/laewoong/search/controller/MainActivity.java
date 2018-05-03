package com.laewoong.search.controller;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.RadioButton;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSearchView;
import com.laewoong.search.R;
import com.laewoong.search.SearchContract;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.BackPressCloseHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity implements SearchContract.Controller {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_LATEST_TAG = "com.laewoong.search.controller.MainActivity.KEY_LATEST_TAG";

    private SearchView  mSearchView;

    private QueryHandler mQueryHandler;
    private BackPressCloseHandler mBackPressCloseHandler;

    private Map<String, QueryResponseController> mResponseControllerMap;

    private String CURRENT_RESPONSE_CONTROLLER_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (SearchView)findViewById(R.id.searchview_query);

        mQueryHandler = ((SearchApplication)getApplication()).getQueryHandler();
        mBackPressCloseHandler = new BackPressCloseHandler(this);

        mResponseControllerMap = new HashMap<String, QueryResponseController>(2);
        mResponseControllerMap.put(WebQueryResponseController.TAG, new WebQueryResponseController(this, mQueryHandler, R.id.container_fragment));
        mResponseControllerMap.put(ImageQueryResponseController.TAG, new ImageQueryResponseController(this, mQueryHandler, R.id.container_fragment, R.id.container_root));

        init();

        // 처음 Activity가 생성된 것이 아니라면 가장 최근 탭으로 복구
        if((savedInstanceState != null) && savedInstanceState.containsKey(KEY_LATEST_TAG)) {

            CURRENT_RESPONSE_CONTROLLER_TAG = savedInstanceState.getString(KEY_LATEST_TAG);
        }
        else {

            CURRENT_RESPONSE_CONTROLLER_TAG = WebQueryResponseController.TAG;
        }

        mResponseControllerMap.get(CURRENT_RESPONSE_CONTROLLER_TAG).show();
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

                mResponseControllerMap.get(CURRENT_RESPONSE_CONTROLLER_TAG).query(query);

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
                .map(event -> WebQueryResponseController.TAG);

        Observable<String> imageButtonObservale = RxView.clicks(findViewById(R.id.button_image))
                .map(event -> ImageQueryResponseController.TAG);

        Observable.merge(webButtonObservable, imageButtonObservale)
                .subscribe(TAG -> {
                    final String query = mSearchView.getQuery().toString().trim();

                    CURRENT_RESPONSE_CONTROLLER_TAG = TAG;
                    QueryResponseController controller = mResponseControllerMap.get(CURRENT_RESPONSE_CONTROLLER_TAG);
                    controller.show();

                    if(query.isEmpty()) {

                        return;
                    }

                    controller.query(query);
                    mSearchView.clearFocus();
                });

        // Init tab view's hint color
        SegmentedGroup tabGroup = (SegmentedGroup)findViewById(R.id.container_tab);
        tabGroup.setTintColor(Color.parseColor("#F06292"));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            mBackPressCloseHandler.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save current tab info to restore when activity restart.
        outState.putString(KEY_LATEST_TAG, CURRENT_RESPONSE_CONTROLLER_TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQueryHandler.addWebQueryResultListener(mResponseControllerMap.get(WebQueryResponseController.TAG).getOnQueryResponseListener());
        mQueryHandler.addImageQueryResultListener(mResponseControllerMap.get(ImageQueryResponseController.TAG).getOnQueryResponseListener());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQueryHandler.removeWebQueryResultListener(mResponseControllerMap.get(WebQueryResponseController.TAG).getOnQueryResponseListener());
        mQueryHandler.removeImageQueryResultListener(mResponseControllerMap.get(ImageQueryResponseController.TAG).getOnQueryResponseListener());
    }

    @Override
    public String getQuery() {
        return mQueryHandler.getQuery();
    }

    @Override
    public List<WebInfo> getWebQueryResponseList() {

        return new LinkedList<WebInfo>(mQueryHandler.getWebInfoList());
    }

    @Override
    public List<ImageInfo> getImageQueryResponseList() {

        return new LinkedList<ImageInfo>(mQueryHandler.getImageInfoList());
    }

    @Override
    public void loadMoreQueryResult() {

        mResponseControllerMap.get(CURRENT_RESPONSE_CONTROLLER_TAG).queryMore();
    }
}