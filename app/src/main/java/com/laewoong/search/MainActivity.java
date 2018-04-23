package com.laewoong.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.laewoong.search.util.BackPressCloseHandler;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnReachedListEndListener, OnSelectedThumbnailListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_IS_IMAGE_TAP = "com.laewoong.search.MainActivity.KEY_IS_IMAGE_TAP";

    private QueryHandler mQueryHandler;

    private View mRootView;
    private View mFragmentContainer;

    private SearchView mSearchView;

    private Button mWebTapButton;
    private Button mImageTapButton;

    private WebResponseFragment     mWebResponseFragment;
    private ImageResponseFragment   mImageResponseFragment;
    private DetailImageFragment     mDetailImageFragment;

    private BackPressCloseHandler mBackPressCloseHandler;

    private boolean mIsImageTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueryHandler = new QueryHandler();

        mRootView = findViewById(R.id.container_root);
        mFragmentContainer = findViewById(R.id.container_fragment);

        mSearchView = (SearchView)findViewById(R.id.searchview_query);

        mWebTapButton = (Button)findViewById(R.id.button_web);
        mImageTapButton = (Button)findViewById(R.id.button_image);

        init();

        mIsImageTap = false;

        if(savedInstanceState != null) {

            mIsImageTap = savedInstanceState.getBoolean(KEY_IS_IMAGE_TAP);
        }

        if(mIsImageTap == true) {
            showImageTap();
        }
        else {
            showWebTap();
        }
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

        mQueryHandler.addWebQueryResultListener(new OnQueryResponseListener() {
            @Override
            public void onSuccessResponse() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mWebResponseFragment.addItems(mQueryHandler.getLatestUpdatedWebInfoList());
                    }
                });
            }
        });

        mQueryHandler.addImageQueryResultListener(new OnQueryResponseListener() {
            @Override
            public void onSuccessResponse() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mImageResponseFragment.addItems(mQueryHandler.getLatestUpdatedImageInfoList());
                    }
                });
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.isEmpty()) {
                    return false;
                }

                mSearchView.clearFocus();

                // TODO : 인터페이스 만들어서 코드 하나로 처리하여 분기문 제거하기.
                if(mIsImageTap == true) {
                    mImageResponseFragment.clearList();
                    mImageResponseFragment.setQuery(query);
                    mQueryHandler.queryImage(query);
                }
                else {
                    mWebResponseFragment.clearList();
                    mWebResponseFragment.setQuery(query);
                    mQueryHandler.queryWeb(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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

        mWebTapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebTap();
                mWebResponseFragment.clearList();
                mQueryHandler.queryWeb(mSearchView.getQuery().toString());
            }
        });

        mImageTapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageTap();
                mImageResponseFragment.clearList();
                mQueryHandler.queryImage(mSearchView.getQuery().toString());
            }
        });

        mBackPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onReachedListEndListener(String keyword) {

        // TODO : 인터페이스 만들어서 코드 하나로 처리하여 분기문 제거하기.
        if(mIsImageTap == true) {
            mQueryHandler.queryImageMore();
        }
        else {
            mQueryHandler.queryWebMore();
        }
    }

    @Override
    public void onSelectedThumbnail(List<ImageInfo> list, int position) {

        FragmentManager fm = getSupportFragmentManager();
        mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

        // create the fragment the first time
        if (mDetailImageFragment == null) {

            mDetailImageFragment = new DetailImageFragment();

            mQueryHandler.addImageQueryResultListener(new OnQueryResponseListener() {
                @Override
                public void onSuccessResponse() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mDetailImageFragment.addItems(mQueryHandler.getImageInfoList());
                        }
                    });
                }
            });
        }

        Bundle args = new Bundle();
        args.putInt(DetailImageFragment.KEY_POSITION, position);
        args.putSerializable(DetailImageFragment.KEY_ITEM_LIST, (LinkedList<ImageInfo>)list);
        mDetailImageFragment.setArguments(args);

        fm.beginTransaction().add(mRootView.getId(), mDetailImageFragment, DetailImageFragment.TAG).addToBackStack(null).commit();
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
        outState.putBoolean(KEY_IS_IMAGE_TAP, mIsImageTap);
    }

    private void showWebTap() {

        mIsImageTap = false;
        getSupportFragmentManager().beginTransaction().replace(mFragmentContainer.getId(), mWebResponseFragment, WebResponseFragment.TAG).commit();

        mSearchView.clearFocus();

    }

    private void showImageTap() {

        mIsImageTap = true;
        getSupportFragmentManager().beginTransaction().replace(mFragmentContainer.getId(), mImageResponseFragment, ImageResponseFragment.TAG).commit();

        mSearchView.clearFocus();
    }
}
