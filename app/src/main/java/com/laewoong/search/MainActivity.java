package com.laewoong.search;

import android.app.SearchManager;
import android.content.Context;
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

public class MainActivity extends AppCompatActivity implements OnReachedListEndListener, OnSelectedThumbnailListener, SearchContract.Presenter {

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
            public void onSuccessResponse() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mWebResponseFragment.updateQueryResult();
                    }
                });
            }
        };

        mImageQueryResponseListener = new OnQueryResponseListener() {
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
    public void onSelectedThumbnail(List<ImageInfo> list, int position) {

        FragmentManager fm = getSupportFragmentManager();
        mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

        // create the fragment the first time
        if (mDetailImageFragment == null) {

            mDetailImageFragment = new DetailImageFragment();
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
}