package com.laewoong.search.presenter;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.laewoong.search.model.OnQueryResponseListener;
import com.laewoong.search.R;
import com.laewoong.search.SearchContract;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.view.DetailImageFragment;
import com.laewoong.search.view.ImageResponseFragment;
import com.laewoong.search.view.ResponseFragment;
import com.laewoong.search.view.WebResponseFragment;

import java.util.LinkedList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends AppCompatActivity implements SearchContract.Presenter {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_IS_IMAGE_TAP = "com.laewoong.search.presenter.MainActivity.KEY_IS_IMAGE_TAP";

    private QueryHandler mQueryHandler;

    private View mRootView;
    private View mFragmentContainer;

    private SearchView mSearchView;

    private RadioButton mWebTabButton;
    private RadioButton mImageTabButton;

    private ResponseFragment mWebResponseFragment;
    private ResponseFragment   mImageResponseFragment;
    private DetailImageFragment     mDetailImageFragment;

    private BackPressCloseHandler mBackPressCloseHandler;

    private boolean mIsImageTap;

    private OnQueryResponseListener mWebQueryResponseListener;
    private OnQueryResponseListener mImageQueryResponseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueryHandler = ((SearchApplication)getApplication()).getQueryHandler();

        mRootView = findViewById(R.id.container_root);
        mFragmentContainer = findViewById(R.id.container_fragment);

        mSearchView = (SearchView)findViewById(R.id.searchview_query);

        mWebTabButton = (RadioButton)findViewById(R.id.button_web);
        mImageTabButton = (RadioButton)findViewById(R.id.button_image);

        SegmentedGroup tabGroup = (SegmentedGroup)findViewById(R.id.container_tab);
        tabGroup.setTintColor(Color.parseColor("#F06292"));

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

        mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

        mWebQueryResponseListener = new OnQueryResponseListener() {

            @Override
            public void onFailNetwork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String errorMessage = getString(R.string.guide_check_network_state);
                        mWebResponseFragment.showErrorMessage(errorMessage);
                    }
                });
            }

            @Override
            public void onSuccessResponse() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebResponseFragment.updateQueryResult();
                    }
                });
            }

            @Override
            public void onErrorQueryResponse(final ErrorCode errorCode) {

                // TODO : send info to server

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String errorMessage = (errorCode == ErrorCode.NAVER_MAX_START_VALUE_POLICY) ? getString(R.string.guide_naver_max_start_value_policy) : getApplicationContext().getString(R.string.guide_internal_error);

                        mWebResponseFragment.showErrorMessage(errorMessage);
                    }
                });
            }

            @Override
            public void onEmptyResponse() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebResponseFragment.handleEmptyQueryResult();
                    }
                });
            }

            @Override
            public void onFinalResponse() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mWebResponseFragment != null) {
                            mWebResponseFragment.handleFinalQueryResult();
                        }
                    }
                });
            }
        };

        mImageQueryResponseListener = new OnQueryResponseListener() {

            @Override
            public void onFailNetwork() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String errorMessage = getString(R.string.guide_check_network_state);
                        if(mImageResponseFragment.isVisible()) {
                            mImageResponseFragment.showErrorMessage(errorMessage);
                        }

                        if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                            mDetailImageFragment.showErrorMessage(errorMessage);
                        }
                    }
                });
            }

            @Override
            public void onSuccessResponse() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(mImageResponseFragment.isVisible()) {
                            mImageResponseFragment.updateQueryResult();
                        }

                        if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                            mDetailImageFragment.updateQueryResult();
                        }
                    }
                });
            }

            @Override
            public void onErrorQueryResponse(final ErrorCode errorCode) {

                // TODO : send info to server

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String errorMessage = (errorCode == ErrorCode.NAVER_MAX_START_VALUE_POLICY) ? getString(R.string.guide_naver_max_start_value_policy) : getApplicationContext().getString(R.string.guide_internal_error);

                        if(mImageResponseFragment.isVisible()) {
                            mImageResponseFragment.showErrorMessage(errorMessage);
                        }

                        if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                            mDetailImageFragment.showErrorMessage(errorMessage);
                        }
                    }
                });
            }

            @Override
            public void onEmptyResponse() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mImageResponseFragment.handleEmptyQueryResult();
                    }
                });
            }

            @Override
            public void onFinalResponse() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mImageResponseFragment.isVisible()) {
                            mImageResponseFragment.handleFinalQueryResult();
                        }

                        if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                            mDetailImageFragment.handleFinalQueryResult();
                        }
                    }
                });
            }
        };

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                query = query.trim();

                if(query.isEmpty()) {
                    return false;
                }

                mSearchView.clearFocus();

                // TODO : 인터페이스 만들어서 코드 하나로 처리하여 분기문 제거하기. or state pattern
                if(mIsImageTap == true) {
                    mImageResponseFragment.clearQueryResult();
                    mQueryHandler.queryImage(query);
                }
                else {
                    mWebResponseFragment.clearQueryResult();
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

        mWebTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String query = mSearchView.getQuery().toString().trim();

                if(query.isEmpty()) {

                    showWebTap();
                    return;
                }

                mWebResponseFragment.clearQueryResult();
                mQueryHandler.queryWeb(query);

                showWebTap();
                mSearchView.clearFocus();
            }
        });

        mImageTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String query = mSearchView.getQuery().toString().trim();

                if(query.isEmpty()) {

                    showImageTap();
                    return;
                }

                mImageResponseFragment.clearQueryResult();
                mQueryHandler.queryImage(query);

                showImageTap();
                mSearchView.clearFocus();
            }
        });

        mBackPressCloseHandler = new BackPressCloseHandler(this);
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

    @Override
    protected void onStart() {
        super.onStart();
        mQueryHandler.addWebQueryResultListener(mWebQueryResponseListener);
        mQueryHandler.addImageQueryResultListener(mImageQueryResponseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQueryHandler.removeWebQueryResultListener(mWebQueryResponseListener);
        mQueryHandler.removeImageQueryResultListener(mImageQueryResponseListener);
    }

    private void showWebTap() {

        mIsImageTap = false;
        getSupportFragmentManager().beginTransaction().replace(mFragmentContainer.getId(), mWebResponseFragment, WebResponseFragment.TAG).commit();
    }

    private void showImageTap() {

        mIsImageTap = true;
        getSupportFragmentManager().beginTransaction().replace(mFragmentContainer.getId(), mImageResponseFragment, ImageResponseFragment.TAG).commit();
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
    public void onSelectedThumbnail(int position) {

        FragmentManager fm = getSupportFragmentManager();
        mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

        // create the fragment the first time
        if (mDetailImageFragment == null) {

            mDetailImageFragment = new DetailImageFragment();
        }

        Bundle args = new Bundle();
        args.putInt(DetailImageFragment.KEY_POSITION, position);
        mDetailImageFragment.setArguments(args);

        fm.beginTransaction().add(mRootView.getId(), mDetailImageFragment, DetailImageFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void loadMoreQueryResult() {

        // TODO : 인터페이스 만들어서 코드 하나로 처리하여 분기문 제거하기.
        if(mIsImageTap == true) {
            mQueryHandler.queryImageMore();
        }
        else {
            mQueryHandler.queryWebMore();
        }
    }
}