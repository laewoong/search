package com.laewoong.search.presenter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.laewoong.search.R;
import com.laewoong.search.model.OnQueryResponseListener;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.view.DetailImageFragment;
import com.laewoong.search.view.ImageResponseFragment;

import java.lang.ref.WeakReference;

/**
 * Created by laewoong on 2018. 4. 28..
 */

public class ImageResponsePresenter implements ResponsePresenter, ImageResponseFragment.OnSelectedThumbnailListener {

    public static final String TAG  = ImageResponsePresenter.class.getSimpleName();

    private WeakReference<AppCompatActivity> mActivity;
    private QueryHandler mQueryHandler;
    private int mContainerId;
    private int mDetailImageContainerId;

    private ImageResponseFragment mImageResponseFragment;
    private DetailImageFragment mDetailImageFragment;
    private OnQueryResponseListener mImageQueryResponseListener;

    public ImageResponsePresenter(AppCompatActivity activity, QueryHandler queryHandler, int containerId, int detailImageContainerId) {

        mActivity = new WeakReference<AppCompatActivity>(activity);
        mQueryHandler = queryHandler;
        mContainerId = containerId;
        mDetailImageContainerId = detailImageContainerId;

        FragmentManager fm = mActivity.get().getSupportFragmentManager();
        mImageResponseFragment = (ImageResponseFragment) fm.findFragmentByTag(ImageResponseFragment.TAG);

        // create the fragment the first time
        if (mImageResponseFragment == null) {

            mImageResponseFragment = new ImageResponseFragment();
        }

        mImageResponseFragment.setOnSelectedThumbnailListener(this);

        mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

        mImageQueryResponseListener = new OnQueryResponseListener() {

            @Override
            public void onFailNetwork() {

                final AppCompatActivity activity = mActivity.get();

                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final String errorMessage = activity.getString(R.string.guide_check_network_state);
                            if(mImageResponseFragment.isVisible()) {
                                mImageResponseFragment.showErrorMessage(errorMessage);
                            }

                            if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                                mDetailImageFragment.showErrorMessage(errorMessage);
                            }
                        }
                    });
                }
            }

            @Override
            public void onSuccessResponse() {

                final AppCompatActivity activity = mActivity.get();

                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
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

            }

            @Override
            public void onErrorQueryResponse(final ErrorCode errorCode) {

                // TODO : send info to server

                final AppCompatActivity activity = mActivity.get();

                if(activity != null) {

                    activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String errorMessage = (errorCode == ErrorCode.NAVER_MAX_START_VALUE_POLICY) ? activity.getString(R.string.guide_naver_max_start_value_policy) : activity.getString(R.string.guide_internal_error);

                        if(mImageResponseFragment.isVisible()) {
                            mImageResponseFragment.showErrorMessage(errorMessage);
                        }

                        if((mDetailImageFragment != null) && (mDetailImageFragment.isVisible())) {
                            mDetailImageFragment.showErrorMessage(errorMessage);
                        }
                    }
                    });
                }
            }

            @Override
            public void onEmptyResponse() {

                final AppCompatActivity activity = mActivity.get();

                    if(activity != null) {

                        activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mImageResponseFragment.handleEmptyQueryResult();
                        }
                    });
                }
            }

            @Override
            public void onFinalResponse() {

                final AppCompatActivity activity = mActivity.get();

                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
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

            }
        };

    }

    @Override
    public void show() {

        final AppCompatActivity activity = mActivity.get();

        if(activity != null) {

            activity.getSupportFragmentManager().beginTransaction().replace(mContainerId, mImageResponseFragment, mImageResponseFragment.TAG).commit();
        }
    }

    @Override
    public void query(String query) {

        if(query.isEmpty()) {

            show();
            return;
        }

        mImageResponseFragment.clearQueryResult();
        mQueryHandler.queryImage(query);

        show();
    }

    @Override
    public void queryMore() {
        mQueryHandler.queryImageMore();
    }

    @Override
    public OnQueryResponseListener getOnQueryResponseListener() {

        return mImageQueryResponseListener;
    }

    @Override
    public void onSelectedThumbnail(int position) {

        final AppCompatActivity activity = mActivity.get();

        if(activity != null) {

            FragmentManager fm = activity.getSupportFragmentManager();
            mDetailImageFragment = (DetailImageFragment) fm.findFragmentByTag(DetailImageFragment.TAG);

            if (mDetailImageFragment == null) {

                mDetailImageFragment = new DetailImageFragment();
            }

            Bundle args = new Bundle();
            args.putInt(DetailImageFragment.KEY_POSITION, position);
            mDetailImageFragment.setArguments(args);

            fm.beginTransaction().add(mDetailImageContainerId, mDetailImageFragment, DetailImageFragment.TAG).addToBackStack(null).commit();
        }
    }
}
