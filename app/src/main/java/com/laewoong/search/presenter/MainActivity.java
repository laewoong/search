package com.laewoong.search.presenter;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;

import com.laewoong.search.OnQueryResponseListener;
import com.laewoong.search.OnReachedListEndListener;
import com.laewoong.search.R;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.model.response.ImageInfo;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.WebInfo;
import com.laewoong.search.util.BackPressCloseHandler;
import com.laewoong.search.view.DetailImageFragment;
import com.laewoong.search.view.ImageResponseFragment;
import com.laewoong.search.view.WebResponseFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnReachedListEndListener, SearchContract.Presenter {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_IS_IMAGE_TAP = "com.laewoong.search.presenter.MainActivity.KEY_IS_IMAGE_TAP";

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
            public void onErrorQueryResponse(ErrorCode errorCode) {

                // TODO : send info to server

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String errorMessage = getApplicationContext().getString(R.string.guide_internal_error);

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

                if(mWebResponseFragment != null) {
                    mWebResponseFragment.handleFinalQueryResult();
                }
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

                        final String errorMessage = getApplicationContext().getString(R.string.guide_internal_error);

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

                if(mImageResponseFragment.isVisible()) {
                    mImageResponseFragment.handleFinalQueryResult();
                }

                if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                    mDetailImageFragment.handleFinalQueryResult();
                }
            }
        };

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.isEmpty()) {
                    return false;
                }

                mSearchView.clearFocus();

                // TODO : 인터페이스 만들어서 코드 하나로 처리하여 분기문 제거하기. or state pattern
                if(mIsImageTap == true) {
                    mQueryHandler.queryImage(query);
                }
                else {
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
                mQueryHandler.queryWeb(mSearchView.getQuery().toString());
            }
        });

        mImageTapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageTap();
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
        return mQueryHandler.getWebInfoList();
    }

    @Override
    public List<ImageInfo> getImageQueryResponseList() {
        return mQueryHandler.getImageInfoList();
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
}