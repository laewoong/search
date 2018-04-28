package com.laewoong.search.presenter;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.RadioButton;

import com.laewoong.search.R;
import com.laewoong.search.SearchContract;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.view.DetailImageFragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends AppCompatActivity implements SearchContract.Presenter {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_LATEST_TAG = "com.laewoong.search.presenter.MainActivity.KEY_LATEST_TAG";

    private SearchView  mSearchView;
    private RadioButton mWebTabButton;
    private RadioButton mImageTabButton;

    private QueryHandler mQueryHandler;
    private BackPressCloseHandler mBackPressCloseHandler;

    private Map<String, ResponsePresenter> mResponseControllerMap;

    private String CURRENT_RESPONSE_PRESENTER_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = (SearchView)findViewById(R.id.searchview_query);

        mWebTabButton = (RadioButton)findViewById(R.id.button_web);
        mImageTabButton = (RadioButton)findViewById(R.id.button_image);

        mQueryHandler = ((SearchApplication)getApplication()).getQueryHandler();
        mBackPressCloseHandler = new BackPressCloseHandler(this);

        mResponseControllerMap = new HashMap<String, ResponsePresenter>(2);
        mResponseControllerMap.put(WebResponsePresenter.TAG, new WebResponsePresenter(this, mQueryHandler, R.id.container_fragment));
        mResponseControllerMap.put(ImageResponsePresenter.TAG, new ImageResponsePresenter(this, mQueryHandler, R.id.container_fragment, R.id.container_root));

        init();

        // 처음 Activity가 생성된 것이 아니라면 가장 최근 탭으로 복구
        if((savedInstanceState != null) && savedInstanceState.containsKey(KEY_LATEST_TAG)) {

            CURRENT_RESPONSE_PRESENTER_TAG = savedInstanceState.getString(KEY_LATEST_TAG);
        }
        else {

            CURRENT_RESPONSE_PRESENTER_TAG = WebResponsePresenter.TAG;
        }

        mResponseControllerMap.get(CURRENT_RESPONSE_PRESENTER_TAG).show();
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

                mResponseControllerMap.get(CURRENT_RESPONSE_PRESENTER_TAG).query(query);

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


        mWebTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String query = mSearchView.getQuery().toString().trim();

                CURRENT_RESPONSE_PRESENTER_TAG = WebResponsePresenter.TAG;
                ResponsePresenter presenter = mResponseControllerMap.get(CURRENT_RESPONSE_PRESENTER_TAG);
                presenter.show();

                if(query.isEmpty()) {

                    return;
                }

                presenter.query(query);
                mSearchView.clearFocus();
            }
        });

        mImageTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String query = mSearchView.getQuery().toString().trim();

                CURRENT_RESPONSE_PRESENTER_TAG = ImageResponsePresenter.TAG;
                ResponsePresenter presenter = mResponseControllerMap.get(CURRENT_RESPONSE_PRESENTER_TAG);
                presenter.show();

                if(query.isEmpty()) {

                    return;
                }

                presenter.query(query);
                mSearchView.clearFocus();
            }
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
        outState.putString(KEY_LATEST_TAG, CURRENT_RESPONSE_PRESENTER_TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQueryHandler.addWebQueryResultListener(mResponseControllerMap.get(WebResponsePresenter.TAG).getOnQueryResponseListener());
        mQueryHandler.addImageQueryResultListener(mResponseControllerMap.get(ImageResponsePresenter.TAG).getOnQueryResponseListener());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQueryHandler.removeWebQueryResultListener(mResponseControllerMap.get(WebResponsePresenter.TAG).getOnQueryResponseListener());
        mQueryHandler.removeImageQueryResultListener(mResponseControllerMap.get(ImageResponsePresenter.TAG).getOnQueryResponseListener());
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

        mResponseControllerMap.get(CURRENT_RESPONSE_PRESENTER_TAG).queryMore();
    }
}