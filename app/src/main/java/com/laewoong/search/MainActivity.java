package com.laewoong.search;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnReachedListEndListener, OnQueryResponseListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private QueryHandler mQueryHandler;

    private SearchView mSearchView;

    private ViewGroup mFragmentContainer;
    private WebResponseFragment mWebResponseFragment;
    private ImageResponseFragment mImageResponseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueryHandler = new QueryHandler();

        mSearchView = (SearchView)findViewById(R.id.searchview_query);
        mFragmentContainer = (ViewGroup)findViewById(R.id.container_fragment);

        init();
    }

    private void init() {

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        mWebResponseFragment = (WebResponseFragment) fm.findFragmentByTag(WebResponseFragment.TAG);

        // create the fragment the first time
        if (mWebResponseFragment == null) {

            mWebResponseFragment = new WebResponseFragment();
        }

        mImageResponseFragment = (ImageResponseFragment) fm.findFragmentByTag(ImageResponseFragment.TAG);

        // create the fragment the first time
        if (mImageResponseFragment == null) {

            mImageResponseFragment = new ImageResponseFragment();
        }

        //fm.beginTransaction().replace(mFragmentContainer.getId(), mWebResponseFragment, WebResponseFragment.TAG).commit();
        fm.beginTransaction().replace(mFragmentContainer.getId(), mImageResponseFragment, ImageResponseFragment.TAG).commit();



        mQueryHandler.setOnQueryResultListener(this);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i(TAG, "========== onQueryTextSubmit: " + query);
                if(query.isEmpty()) {
                    return false;
                }

//                mWebResponseFragment.clearList();
//                mWebResponseFragment.setQuery(query);
//                mQueryHandler.queryWeb(query);

                mImageResponseFragment.clearList();
                mImageResponseFragment.setQuery(query);
                mQueryHandler.queryImage(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "========== onQueryTextChange: " + newText);
                return false;
            }
        });


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryRefinementEnabled(true);


    }

    @Override
    public void onReachedListEndListener(String keyword) {
        //mQueryHandler.queryWeb(keyword);
        mQueryHandler.queryImage(keyword);
    }

    @Override
    public void onResponseWeb(final List<WebInfo> list) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebResponseFragment.addItems(list);
            }
        });
    }

    @Override
    public void onResponseImage(final List<ImageInfo> list) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageResponseFragment.addItems(list);
            }
        });
    }
}
