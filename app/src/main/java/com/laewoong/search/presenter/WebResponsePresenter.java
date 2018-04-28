package com.laewoong.search.presenter;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.laewoong.search.R;
import com.laewoong.search.model.OnQueryResponseListener;
import com.laewoong.search.model.QueryHandler;
import com.laewoong.search.model.response.ErrorCode;
import com.laewoong.search.view.WebResponseFragment;

import java.lang.ref.WeakReference;

/**
 * Created by laewoong on 2018. 4. 28..
 */

public class WebResponsePresenter implements ResponsePresenter {

    public static final String TAG  = WebResponsePresenter.class.getSimpleName();

    private WeakReference<AppCompatActivity> mActivity;
    private QueryHandler mQueryHandler;
    private int mContainerId;

    private WebResponseFragment mWebResponseFragment;
    private OnQueryResponseListener mOnQueryResponseListener;

    public WebResponsePresenter(AppCompatActivity activity, QueryHandler queryHandler, int containerId) {

        mActivity = new WeakReference<AppCompatActivity>(activity);
        mQueryHandler = queryHandler;
        mContainerId = containerId;

        FragmentManager fm = mActivity.get().getSupportFragmentManager();
        mWebResponseFragment = (WebResponseFragment) fm.findFragmentByTag(WebResponseFragment.TAG);

        if (mWebResponseFragment == null) {

            mWebResponseFragment = new WebResponseFragment();
        }

        mOnQueryResponseListener = new OnQueryResponseListener() {

            @Override
            public void onFailNetwork() {

                final AppCompatActivity activity = mActivity.get();

                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final String errorMessage = activity.getString(R.string.guide_check_network_state);
                            if(mWebResponseFragment.isVisible()) {
                                mWebResponseFragment.showErrorMessage(errorMessage);
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

                            if(mWebResponseFragment.isVisible()) {
                                mWebResponseFragment.updateQueryResult();
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

                        if(mWebResponseFragment.isVisible()) {
                            mWebResponseFragment.showErrorMessage(errorMessage);
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

                            if(mWebResponseFragment.isVisible()) {
                                mWebResponseFragment.handleEmptyQueryResult();
                            }
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
                            if(mWebResponseFragment.isVisible()) {
                                mWebResponseFragment.handleFinalQueryResult();
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

            activity.getSupportFragmentManager().beginTransaction().replace(mContainerId, mWebResponseFragment, mWebResponseFragment.TAG).commit();
        }
    }

    @Override
    public void query(String query) {

        if(query.isEmpty()) {

            show();
            return;
        }

        mWebResponseFragment.clearQueryResult();
        mQueryHandler.queryWeb(query);
    }

    @Override
    public void queryMore() {
        mQueryHandler.queryWebMore();
    }

    @Override
    public OnQueryResponseListener getOnQueryResponseListener() {

        return mOnQueryResponseListener;
    }
}
